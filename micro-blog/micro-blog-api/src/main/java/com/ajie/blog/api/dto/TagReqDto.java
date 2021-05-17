package com.ajie.blog.api.dto;

import com.ajie.commons.dto.BaseReqDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@ApiModel(value = "TagReqDto", description = "标签入参")
@Getter
@Setter
public class TagReqDto extends BaseReqDto {
    @ApiModelProperty(value = "标签组")
    private List<TagDto> tagList;

    public List<TagDto> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagDto> tagList) {
        this.tagList = tagList;
    }
}
