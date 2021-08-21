package com.ajie.blog.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "BlogReqDto", description = "博文入参")
@Getter
@Setter
public class DraftBlogReqDto extends BlogReqDto {

    @ApiModelProperty(value = "是否强制保存草稿，0否1是，当草稿已经存在时使用")
    private Integer forceSaveDraft;

    /**
     * 是否强制保存
     *
     * @return
     */
    public boolean isForceSave() {
        return null == forceSaveDraft ? false : Integer.valueOf(1).equals(forceSaveDraft);
    }
}
