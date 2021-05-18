package com.ajie.blog.api.po;

import com.ajie.commons.po.BasePO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("mb_comment")
public class CommentPO extends BasePO {

    /**
     * 博文ID
     */
    private Long blogId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 父评论ID
     */

    private Long parentId;
    /**
     * 内容
     */
    private String content;
}