package com.ajie.blog.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class MigrateDto implements Serializable {
    private Integer id;
    /**
     * 用户ID
     */

    private Long userId;
    /**
     * 标题
     */

    private String title;
    /**
     * 内容
     */

    private String content;
    /**
     * 摘要
     */

    private String abstractContent;

    /**
     * 类型，1正常，2私密
     */

    private Integer type;

    private String labelStrs;

    private Date createTime;

    private Integer readNum;

    private Integer commentNum;
}
