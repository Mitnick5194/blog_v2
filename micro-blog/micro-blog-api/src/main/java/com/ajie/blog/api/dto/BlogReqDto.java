package com.ajie.blog.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel(value = "BlogReqDto", description = "博文入参")
@Getter
@Setter
public class BlogReqDto extends BlogDto {

    /**
     * 标签组
     */
    @ApiModelProperty(value = "标签组")
    List<TagDto> tagList;
}
