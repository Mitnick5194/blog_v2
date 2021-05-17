package com.ajie.blog.api.po;

import com.ajie.commons.po.BasePO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("mb_blog_tag")
public class BlogTagPO extends BasePO {
    /**
     * 标签名
     */

    private String tagName;
    /**
     * 标签ID
     */

    private Long tagId;
    /**
     * 博客ID
     */

    private Long blogId;
}