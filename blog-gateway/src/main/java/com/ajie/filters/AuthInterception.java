package com.ajie.filters;

import com.ajie.config.Properties;
import com.ajie.dto.JwtAccount;
import com.ajie.exception.VerifyException;
import com.ajie.utils.JwtUtil;
import com.ajie.utils.PathUtil;
import com.ajie.utils.RandomUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Component
public class AuthInterception implements GlobalFilter, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(AuthInterception.class);
    private static final String LOGINRESP = "{\"code\":401,\"msg\":\"未登录\",\"data\":\"\"}";
    private static final String FORBIDDEN = "{\"code\":403,\"msg\":\"权限不足\",\"data\":\"\"}";

    private static final String SERVER_ERROR = "{\"code\":500,\"msg\":\"服务器异常\",\"data\":\"\"}";
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
            //日志追踪7
            String reqId = RandomUtil.getRandomString(12, true);
            //日志追踪id
            MDC.put("reqId", reqId);
            //放入头部
            ServerHttpRequest.Builder mutate = request.mutate().header("reqId", reqId);
            exchange = exchange.mutate().request(mutate.build()).build();
            //验证路径是否要登录
            if (!PathUtil.assertAuth(request.getPath().toString())) {
                //不需要登录，放行
                return chain.filter(exchange);
            }
            String token = request.getHeaders().getFirst("auth");
            if (StringUtils.isBlank(token)) {
                return write(LOGINRESP, exchange);
            }
            if (testHeader.equals(token) && isDev()) {
                return chain.filter(exchange);
            }
            //获取jwt信息
            JwtAccount account = JwtUtil.verifyToken(token, Properties.tokenSecret);
            //将用户信息放入头部
            mutate.header(TICKET_KEY, JSON.toJSONString(account));
            return chain.filter(exchange);
        }/* catch (VerifyException e) {
            logger.error("用户验证异常", e);
            return write(LOGINRESP, exchange);
        } */catch (Throwable e) {
            logger.error("", e);
            return write(SERVER_ERROR, exchange);
        }
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
}
