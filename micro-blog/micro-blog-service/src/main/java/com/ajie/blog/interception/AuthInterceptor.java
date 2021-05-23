package com.ajie.blog.interception;

import com.ajie.blog.config.Properties;
import com.ajie.commons.interception.AbstractAuthInterceptor;
import com.ajie.commons.utils.PathUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthInterceptor extends AbstractAuthInterceptor {
    @Override
    protected boolean assertAuth(HttpServletRequest request) {
        //验证路径是否要登录
        if (!PathUtil.assertAuth(request.getRequestURI(), Properties.ignoreAuthPath, Properties.authPath)) {
            //不需要登录
            return false;
        }
        return true;
    }

}
