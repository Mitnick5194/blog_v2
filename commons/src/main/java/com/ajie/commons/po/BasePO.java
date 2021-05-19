package com.ajie.commons.po;

import com.ajie.commons.utils.UserInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础持久PO
 */
@Getter
@Setter
public class BasePO implements Serializable {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建人
     */
    private String createPerson;
    /**
     * 更新人
     */
    private String updatePerson;

    /**
     * 逻辑删除标志，1删除，0未删除
     */
    private int del = 0;

    private static final String EQ_KEY = "equal";
    private static final String ORDER_BY_KEY = "order_by";

    /**
     * 方法缓存，不用每次都用反射获取
     */
    private static Map<String, Method> methods = new ConcurrentHashMap<>();

    /**
     * po对象属性缓存
     */
    private static Map<String, List<Field>> poFields = new ConcurrentHashMap<>();

    /**
     * 填充公共字段，一般在插入前一步执行
     */
    public void createFill() {
        Long userId = UserInfoUtil.getUserId();
        if (null != userId) {
            this.createPerson = String.valueOf(userId);
            this.updatePerson = String.valueOf(userId);
        }
        this.createTime = new Date();
        this.updateTime = new Date();
    }

    public void updateFill() {
        Long userId = UserInfoUtil.getUserId();
        if (null != userId) {
            this.updatePerson = String.valueOf(userId);
        }
        this.updateTime = new Date();
    }

    /**
     * 转换成mybatis-plus查询参数，并添加del参数和按创建时间排序
     *
     * @return
     * @deprecated commons引入了mp包，不用反射了，使用wrap方法代替
     */
    @Deprecated
    public <T> T toQueryWrap() {
        Map<String, Object> map = new HashMap<>();
        try {
            Class<?> aClass = this.getClass();
            List<Field> fields = getFields(aClass);
            for (Field f : fields) {
                f.setAccessible(true);
                String name = f.getName();
                Object o = f.get(this);
                if (Objects.isNull(o)) {
                    continue;
                }
                map.put(camelCaseToUnderline(name), o);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            Class clazz = Class.forName("com.baomidou.mybatisplus.core.conditions.query.QueryWrapper");
            Object o = clazz.newInstance();
            if (map.isEmpty()) {
                return (T) o;
            }
            Method m = getEqMethod(clazz);
            Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> next = it.next();
                String key = next.getKey();
                Object value = next.getValue();
                m.invoke(o, true, key, value);
            }
            m.invoke(o, true, "del", 0);
            Method orderBy = getOrderByMethod(clazz);
            orderBy.invoke(0, true, false, "create_time");
            return (T) o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends BasePO> QueryWrapper<T> wrap(Class<T> clazz) {
        Map<String, Object> map = new HashMap<>();
        try {
            Class<?> aClass = this.getClass();
            List<Field> fields = getFields(aClass);
            for (Field f : fields) {
                f.setAccessible(true);
                String name = f.getName();
                Object o = f.get(this);
                if (Objects.isNull(o)) {
                    continue;
                }
                map.put(camelCaseToUnderline(name), o);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        QueryWrapper wrap = new QueryWrapper();
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> next = it.next();
            String key = next.getKey();
            Object value = next.getValue();
            wrap.eq(key, value);
        }
        wrap.eq("del", 0);
        wrap.orderByDesc("create_time");
        return wrap;
    }

    private Method getEqMethod(Class clazz) throws NoSuchMethodException {
        Method method = methods.get(EQ_KEY);
        if (null != method) {
            return method;
        }
        Method m = clazz.getSuperclass().getMethod("eq", boolean.class, Object.class, Object.class);
        methods.put(EQ_KEY, m);//并发也不怕，最多让后一个替换掉，反正都一样的
        return m;
    }

    private Method getOrderByMethod(Class<?> clazz) throws NoSuchMethodException {
        Method method = methods.get(ORDER_BY_KEY);
        if (null != method) {
            return method;
        }
        Method m = clazz.getSuperclass().getMethod("orderBy", boolean.class, boolean.class, Object[].class);
        methods.put(ORDER_BY_KEY, m);//并发也不怕，最多让后一个替换掉，反正都一样的
        return m;
    }

    private List<Field> getFields(Class<?> clazz) {
        String name = clazz.getName();
        List<Field> fields = poFields.get(name);
        if (CollectionUtils.isNotEmpty(fields)) {
            return fields;
        }
        List<Field> list = new ArrayList<>();
        do {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : declaredFields) {
                //过滤掉final和static属性
                if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                list.add(f);
            }
            clazz = clazz.getSuperclass();
        } while (null != clazz);
        poFields.put(name, list);
        return list;
    }

    /**
     * 将驼峰转换成下划线
     *
     * @param property
     * @return
     */
    private static String camelCaseToUnderline(String property) {
        StringBuilder sb = new StringBuilder();
        for (char c : property.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                //小写
                sb.append(c);
                continue;
            }
            sb.append("_").append((char) (c + 32));
        }
        return sb.toString();
    }

    static class Test extends BasePO {
        private String test;
    }

    public static void main(String[] args) {
        Test t = new Test();
        Test test = t.toQueryWrap();
    }
}
