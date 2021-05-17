package com.ajie.blog.aop;

import com.ajie.commons.po.BasePO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * mapper操作切面
 */
@Aspect
@Component
public class MapperAspect {

    @Around("execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.insert(..))")
    public Object insert(ProceedingJoinPoint point) throws Throwable {
        Object param = point.getArgs()[0];
        if (param instanceof BasePO) {
            BasePO basePO = (BasePO) param;
            basePO.createFill();
        }
        return point.proceed();
    }

    @Around("execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.update(..))")
    public Object update(ProceedingJoinPoint point) throws Throwable {
        Object param = point.getArgs()[0];
        if (param instanceof BasePO) {
            BasePO basePO = (BasePO) param;
            basePO.updateFill();
        }
        return point.proceed();
    }
}
