package com.ajie.blog.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@ApiModel(value = "DraftBlogDto", description = "DraftBlogDto")
@Getter
@Setter
public class DraftBlogDto implements Serializable {

    /**
     * 主键ID
     */
    @ApiModelProperty(name = "id", value = "主键ID")
    private Long id;

    /**
     * 源博文ID
     */
    @ApiModelProperty(name = "refBlogId", value = "源博文ID")
    private Long refBlogId;
    /**
     * 用户ID
     */
    @ApiModelProperty(name = "userId", value = "用户ID")
    private Long userId;
    /**
     * 标题
     */
    @ApiModelProperty(name = "title", value = "标题")
    private String title;
    /**
     * 内容
     */
    @ApiModelProperty(name = "content", value = "内容")
    private String content;
    /**
     * 摘要
     */
    @ApiModelProperty(name = "abstractContent", value = "摘要")
    private String abstractContent;
    /**
     * 类型，1正常，2私密
     */
    @ApiModelProperty(name = "type", value = "类型，1正常，2私密")
    private Integer type;
}