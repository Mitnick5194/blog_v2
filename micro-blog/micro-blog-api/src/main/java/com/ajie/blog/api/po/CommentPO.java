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
    /**
     * 状态，1正常，2草稿
     */

    private Integer status;
    /**
     * 用户名称
     */

    private String userName;
    /**
     * 用户头像链接
     */

    private String userHeaderUrl;
}