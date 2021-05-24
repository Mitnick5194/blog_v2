package com.ajie.blog.api.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "BlogDto", description = "BlogDto")
@Getter
@Setter
public class BlogDto implements Serializable {
    /**
     * 主键ID
     */
    @ApiModelProperty(name = "id", value = "主键ID")
    private Long id;
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

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private Date createTime;


}