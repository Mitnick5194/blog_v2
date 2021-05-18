package com.ajie.blog.api.po;

import com.ajie.commons.po.BasePO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("mb_draft_blog")
public class DraftBlogPO extends BasePO {

    /**
     * 源博文ID
     */
    private Long refBlogId;
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
}