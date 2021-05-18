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
     * 主键ID
     */
    @ApiModelProperty(name = "id", value = "主键ID")
    private Long id;
    /**
     * 博文ID
     */
    @ApiModelProperty(name = "blogId", value = "博文ID")
    private Long blogId;
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
}