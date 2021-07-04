package com.ajie.filters;

import com.ajie.config.Properties;
import com.ajie.dto.JwtAccount;
import com.ajie.exception.VerifyException;
import com.ajie.utils.JwtUtil;
import com.ajie.utils.PathUtil;
import com.ajie.utils.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthInterception implements GlobalFilter, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(AuthInterception.class);
    private static final String LOGINRESP = "{\"code\":401,\"msg\":\"未登录\",\"data\":\"\"}";
    private static final String FORBIDDEN = "{\"code\":403,\"msg\":\"权限不足\",\"data\":\"\"}";
    private static final String AUTH_SUCCESS = "{\"code\":200,\"msg\":\"\",\"data\":\"\"}";

    private static final String SERVER_ERROR = "{\"code\":500,\"msg\":\"服务器异常\",\"data\":\"\"}";

    private static final String AUTH_PATH = "auth";

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 解析后的用户信息
     */
    private static final String TICKET_KEY = "ticket";
    //测试头部，用于api调试，无需认证和判断权限
    private static final String testHeader = "test";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getPath().toString();
            String token = request.getHeaders().getFirst("auth");
            if (isAuth(path)) {
                if (StringUtils.isBlank(token)) {
                    return write(LOGINRESP, exchange);
                }
                JwtAccount account = getAndCheckAccount(token);
                if (null == account) {
                    //token黑名单
                    return write(LOGINRESP, exchange);
                }
                return write(refreshToken(account, token), exchange);
            }
            //日志追踪7
            String reqId = RandomUtil.getRandomString(12, true);
            //日志追踪id
            MDC.put("reqId", reqId);
            //放入头部
            ServerHttpRequest.Builder mutate = request.mutate().header("reqId", reqId);
            exchange = exchange.mutate().request(mutate.build()).build();
            //验证路径是否要登录
            if (!PathUtil.assertAuth(request.getPath().toString())) {
                //不需要登录，看看是否有token，如果有token也要解析一下，因为有些页面会根据用户是否有登录而出现不同的展示（如编辑和删除）
                try {
                    JwtAccount account = getAndCheckAccount(token);
                    //解析成功，将用户塞进ticket
                    if(null != account){
                        mutate.header(TICKET_KEY, JSON.toJSONString(account));
                    }
                }catch (Exception e){
                    //解析失败，登录过期了或者token解析有问题，当不登录处理就好了
                }
                Mono<Void> filter = chain.filter(exchange);
                return filter;
            }
            if (StringUtils.isBlank(token)) {
                return write(LOGINRESP, exchange);
            }
            if (testHeader.equals(token) && isDev()) {
                return chain.filter(exchange);
            }
            JwtAccount account = getAndCheckAccount(token);
            if (null == account) {
                //token黑名单
                return write(LOGINRESP, exchange);
            }
            //将用户信息放入头部
            mutate.header(TICKET_KEY, JSON.toJSONString(account));
            Mono<Void> filter = chain.filter(exchange);
            return filter;
        } catch (VerifyException e) {
            logger.error("用户验证异常", e);
            return write(LOGINRESP, exchange);
        } catch (Throwable e) {
            logger.error("", e);
            return write(SERVER_ERROR, exchange);
        }
    }

    private boolean isAuth(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }
        int idx = path.lastIndexOf("/auth");
        if (-1 == idx) {
            return false;
        }
        String substring = path.substring(idx + 1);
        return AUTH_PATH.equals(substring);
    }

    /**
     * 验证当前token是否退出登录或者进了黑名单
     *
     * @param token
     * @return
     */
    private JwtAccount getAndCheckAccount(String token) {
        //验证token
        //获取jwt信息
        JwtAccount account = JwtUtil.verifyToken(token, Properties.tokenSecret);
        String s = stringRedisTemplate.opsForValue().get(account.getSign());
        if (null != s) {
            return null;
        }
        return account;
    }

    private Mono<Void> write(String msg, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        byte[] bits = msg.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isDev() {
        String env = System.getProperty("env");
        return "dev".equals(env);
    }

    /**
     * 刷新token，如果token已经过期，不会到这里，在校验token获取account时已经异常了
     *
     * @return
     */
    private String refreshToken(JwtAccount account, String oldToken) {
        JSONObject obj = new JSONObject();
        obj.put("code", 200);
        obj.put("msg", "");
        Date expire = account.getExpire();
        //只有当剩余时间小于20分钟，才刷新
        if (expire.getTime() - System.currentTimeMillis() > 20 * 60 * 1000) {
            obj.put("data", oldToken);
        } else {
            //刷新token
            String token = JwtUtil.createToken(Properties.tokenSecret, account);
            obj.put("data", token);
        }
        return obj.toString();
    }
}
