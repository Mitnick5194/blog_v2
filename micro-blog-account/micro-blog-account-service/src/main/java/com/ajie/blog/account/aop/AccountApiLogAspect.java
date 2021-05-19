package com.ajie.blog.account.aop;

import com.ajie.commons.aop.GlobalLogAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AccountApiLogAspect extends GlobalLogAspect {
    @Override
    @Pointcut("execution(* com.ajie.blog.account.controller.*.*(..))")
    protected void pointCut() {

    }
}
