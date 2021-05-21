package com.ajie.filters;

import com.ajie.config.Properties;
import com.ajie.dto.JwtAccount;
import com.ajie.utils.JwtUtil;
import com.ajie.utils.RandomUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
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
    private static final String LOGINRESP = "{\"code\":401,\"msg\":\"未登录\",\"data\":\"\"}";
    private static final String FORBIDDEN = "{\"code\":403,\"msg\":\"权限不足\",\"data\":\"\"}";
    /**
     * 解析后的用户信息
     */
    private static final String TICKET_KEY = "ticket";
    //测试头部，用于api调试，无需认证和判断权限
    private static final String testHeader = "test";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        //日志追踪
        String reqId = RandomUtil.getRandomString(12, true);
        //日志追踪id
        MDC.put("reqId", reqId);
        request.getQueryParams().add("reqId", reqId);
        List<String> auths = request.getHeaders().get("auth");
        String token = null;
        if (CollectionUtils.isEmpty(auths) || StringUtils.isBlank(token = auths.get(0))) {
            return write(LOGINRESP, exchange);
        }
        if (testHeader.equals(token) && isDev()) {
            return chain.filter(exchange);
        }
        //获取jwt信息
        try {
            JwtAccount account = JwtUtil.verifyToken(token, Properties.tokenSecret);
            //将用户信息放入头部
            request.getHeaders().put(TICKET_KEY, Collections.singletonList(JSON.toJSONString(account)));
        } catch (Exception e) {
            return write(LOGINRESP, exchange);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> write(String msg, ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        byte[] bits = LOGINRESP.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isDev() {
        String dev = System.getProperty("dev");
        return "true".equals(dev);
    }
}
