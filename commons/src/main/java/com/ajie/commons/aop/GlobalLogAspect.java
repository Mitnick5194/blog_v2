package com.ajie.commons.aop;

import com.ajie.commons.RestResponse;
import com.ajie.commons.exception.CommonException;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局日志处理
 */
public abstract class GlobalLogAspect {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取切面
     */
    protected abstract void pointCut();

    @Around("pointCut()")
    public Object logAround(ProceedingJoinPoint point) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String ip = request.getRemoteAddr();
        String classMethod = new StringBuilder().append(point.getSignature().getDeclaringTypeName()).append(".").
                append(point.getSignature().getName()).toString();
        StringBuilder sb = new StringBuilder();
        sb.append("uri:").append(uri).append("，method:").append(method).
                append(",ip:").append(ip).append(",classMethod").append(classMethod).append(",bizParam:").append(JSON.toJSON(point.getArgs()));
        logger.info("API start===> {}", sb.toString());
        Long start = System.currentTimeMillis();
        Long end;
        Object proceed;
        try {
            proceed = point.proceed();
            end = System.currentTimeMillis();
        } catch (Throwable e) {
            //判断异常类型
            RestResponse resp = new RestResponse();
            if (e instanceof CommonException) {
                CommonException ex = (CommonException) e;
                Integer code = ex.getCode();
                if (null == code) {
                    code = CommonException.SERVICE_ERROR_CODE;
                }
                String msg = ex.getMsg();
                if (StringUtils.isBlank(msg)) {
                    msg = CommonException.SERVICE_ERROR_MSG;
                }
                resp.setCode(code);
                resp.setMsg(msg);
                Map<String, String> map = new HashMap<>();
                map.put("path", uri);
                map.put("exception", e.getClass().getCanonicalName());
                map.put("error", e.getMessage());
            } else {
                String msg = e.getMessage();
                if (StringUtils.isBlank(msg)) {
                    msg = CommonException.SERVICE_ERROR_MSG;
                }
                resp.setCode(CommonException.SERVICE_ERROR_CODE);
                resp.setMsg(msg);
                Map<String, String> map = new HashMap<>();
                map.put("path", uri);
                map.put("exception", e.getClass().getCanonicalName());
                map.put("error", e.getMessage());
            }
            proceed = resp;
        }
        sb.append(",response:").append(JSON.toJSON(proceed));
        logger.info("API end ===> {}", sb.toString());
        return proceed;
    }
}
