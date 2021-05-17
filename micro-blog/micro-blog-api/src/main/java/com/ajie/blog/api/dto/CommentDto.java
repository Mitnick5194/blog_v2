package com.ajie.blog.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@ApiModel(value = "CommentDto", description = "CommentDto")
@Getter
@Setter
public class CommentDto implements Serializable {
    /**
     * 用户ID
     */
    @ApiModelProperty(name = "userId", value = "用户ID")
    private Long userId;
    /**
     * 父评论ID
     */
    @ApiModelProperty(name = "parentId", value = "父评论ID")
    private Long parentId;
    /**
     * 内容
     */
    @ApiModelProperty(name = "content", value = "内容")
    private String content;
    /**
     * 状态，1正常，2草稿
     */
    @ApiModelProperty(name = "status", value = "状态，1正常，2草稿")
    private Integer status;
    /**
     * 用户名称
     */
    @ApiModelProperty(name = "userName", value = "用户名称")
    private String userName;
    /**
     * 用户头像链接
     */
    @ApiModelProperty(name = "userHeaderUrl", value = "用户头像链接")
    private String userHeaderUrl;
}