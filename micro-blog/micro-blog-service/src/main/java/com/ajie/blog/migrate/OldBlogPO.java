package com.ajie.blog.migrate;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@TableName("tb_blog")
public class OldBlogPO {

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
}
