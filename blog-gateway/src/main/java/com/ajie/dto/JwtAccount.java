package com.ajie.commons.dto;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class JwtAccount {
    /**
     * 用户ID
     */
    private Long id;
    /**
     * 用户名
     */
    private String accountName;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像链接
     */
    private String headerUrl;

    /**
     * 附加信息，json格式
     */
    private String attach;

    public <T> T getAttachObject(Class<T> clazz) {
        if (StringUtils.isBlank(attach)) {
            return (T) null;
        }
        return JSON.parseObject(attach, clazz);
    }

}
