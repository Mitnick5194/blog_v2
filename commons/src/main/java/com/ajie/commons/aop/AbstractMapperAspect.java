package com.ajie.commons.aop;

import com.ajie.commons.constant.TableConstant;
import com.ajie.commons.enums.CommonsExceptionEmun;
import com.ajie.commons.exception.CommonException;
import com.ajie.commons.po.BasePO;
import com.ajie.commons.utils.UserInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * mapper操作切面，子类负责注入
 */
public abstract class AbstractMapperAspect {
    /**
     * 忽略填充，数据迁移使用
     */
    public static final String IGNORE_FILL = "ignore_fill";

    /**
     * 拦截插入接口
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.insert(..))")
    public Object insert(ProceedingJoinPoint point) throws Throwable {
        if (IGNORE_FILL.equals(System.getProperty(IGNORE_FILL))) {
            return point.proceed();
        }
        Object param = point.getArgs()[0];
        if (param instanceof BasePO) {
            BasePO basePO = (BasePO) param;
            basePO.createFill();
        }
        return point.proceed();
    }

    /**
     * 拦截更新接口
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.update*(..))")
    public Object update(ProceedingJoinPoint point) throws Throwable {
        if (IGNORE_FILL.equals(System.getProperty(IGNORE_FILL))) {
            return point.proceed();
        }
        Object param = point.getArgs()[0];
        if (param instanceof BasePO) {
            BasePO basePO = (BasePO) param;
            checkBelong(point, basePO);
            basePO.updateFill();
        }
        return point.proceed();
    }

    /* */

    /**
     * 属主判断，删除和修改的时候需要判断当前登录这是否为数据的拥有着，防止篡改
     *
     * @return
     */
    private boolean checkBelong(ProceedingJoinPoint point, BasePO basePO) {
        Long id = UserInfoUtil.getUserId();
       /* if (null == id) {
            throw new CommonException(CommonsExceptionEmun.BELONG_NOT_MATCH.getCode(), CommonsExceptionEmun.BELONG_NOT_MATCH.getMsg());
        }*/
        //这里拿到的是//com.baomidou.mybatisplus.core.override.MybatisMapperProxy，这是mapper代理后的对象，可转成mapper
        Object target = point.getTarget();
        try {
            Method m = target.getClass().getMethod("selectById", Serializable.class);
            BasePO getById = (BasePO) m.invoke(target, basePO.getId());
            if (null == getById) {
                throw new CommonException(CommonsExceptionEmun.PARAM_ERROR.getCode(), CommonsExceptionEmun.PARAM_ERROR.getMsg());
            }
            String createPerson = getById.getCreatePerson();
            if (StringUtils.isBlank(createPerson)) {
                return false;
            }
            return id.equals(Long.valueOf(createPerson));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 拦截根据ID查询接口
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.selectById(..))")
    public Object selectById(ProceedingJoinPoint point) throws Throwable {
        Object ret = point.proceed();
        if (ret instanceof BasePO) {
            BasePO po = (BasePO) ret;
            int del = po.getDel();
            if (TableConstant.DEL == del) {
                //被删了
                return null;
            }
        }
        return ret;
    }

    /**
     * 拦截查询接口
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.baomidou.mybatisplus.core.mapper.BaseMapper.select*(..))")
    public Object query(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        for (Object obj : args) {
            fillBasePoDel(obj);
        }
        return point.proceed();
    }

    private void fillBasePoDel(Object obj) {
        if (obj instanceof BasePO) {
            BasePO po = (BasePO) obj;
            po.setDel(TableConstant.NOT_DEL);
            return;
        }
        if (obj instanceof QueryWrapper) {
            QueryWrapper basePO = (QueryWrapper) obj;
            Object entity = basePO.getEntity();
            if (entity instanceof BasePO) {
                BasePO po = (BasePO) obj;
                po.setDel(TableConstant.NOT_DEL);
            }
            return;
        }
        if (obj instanceof Map) {
            Map<Object, Object> map = (Map<Object, Object>) obj;
            map.put("del", TableConstant.NOT_DEL);
        }
    }
}
