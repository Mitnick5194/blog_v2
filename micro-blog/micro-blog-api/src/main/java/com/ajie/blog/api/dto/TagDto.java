package com.ajie.blog.api.dto;

import com.ajie.commons.dto.BaseReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 标签
 */
@ApiModel(value = "TagDto", description = "标签")
@Getter
@Setter
public class TagDto extends BaseReqDto {
    @ApiModelProperty(value = "主键ID")
    private Long id;
    @ApiModelProperty(value = "标签名")
    private String tag;
    @ApiModelProperty(value = "博客数")
    private int blogCount;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
