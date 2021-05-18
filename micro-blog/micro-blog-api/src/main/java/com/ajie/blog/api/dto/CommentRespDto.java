package com.ajie.blog.api.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@ApiModel(value = "CommentRespDto", description = "CommentRespDto")
@Getter
@Setter
public class CommentRespDto extends CommentDto {

    /**
     * 创建时间
     */
    @ApiModelProperty(name = "createTime", value = "创建时间")
    private Date createTime;


    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 用户头像链接
     */
    @ApiModelProperty(value = "用户头像链接")
    private String userHeaderUrl;

    /**
     * 子评论
     */
    @ApiModelProperty(value = "子评论")
    private List<CommentRespDto> children;
}
