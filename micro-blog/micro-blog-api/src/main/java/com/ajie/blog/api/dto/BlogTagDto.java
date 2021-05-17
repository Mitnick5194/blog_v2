package com.ajie.blog.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@ApiModel(value = "BlogTagDto", description = "BlogTagDto")
@Getter
@Setter
public class BlogTagDto implements Serializable {
    /**
     * 标签名
     */
    @ApiModelProperty(name = "tagName", value = "标签名")
    private String tagName;
    /**
     * 标签ID
     */
    @ApiModelProperty(name = "tagId", value = "标签ID")
    private Long tagId;
    /**
     * 博客ID
     */
    @ApiModelProperty(name = "blogId", value = "博客ID")
    private Long blogId;
}