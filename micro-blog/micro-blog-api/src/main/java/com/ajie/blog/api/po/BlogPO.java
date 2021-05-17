package com.ajie.blog.api.po;

import com.ajie.commons.po.BasePO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("mb_blog")
public class BlogPO extends BasePO {
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
     * 状态，1正常，2草稿
     */

    private Integer status;
    /**
     * 类型，1正常，2私密
     */

    private Integer type;
}