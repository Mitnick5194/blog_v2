package com.ajie.blog.aop;

import com.ajie.commons.aop.GlobalLogAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BlogApiLogAspect extends GlobalLogAspect {
    @Override
    @Pointcut("execution(* com.ajie.blog.controller.*.*(..))")
    protected void pointCut() {

    }
}
