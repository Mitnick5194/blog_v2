package com.ajie.blog.api.dto;

import com.ajie.commons.dto.BasePageReqDto;
import com.ajie.commons.dto.BaseReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询
 */
@ApiModel(value = "BlogQueryReqDto", description = "查询博文")
@Getter
@Setter
public class BlogQueryReqDto extends BasePageReqDto {
    @ApiModelProperty(value = "搜索关键字")
    private String keyword;
    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private List<Long> tagList;
}
