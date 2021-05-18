package com.ajie.blog.api.dto;

import com.ajie.commons.dto.BasePageReqDto;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "CommentReqDto", description = "CommentReqDto")
@Getter
@Setter
public class CommentReqDto extends BasePageReqDto {
    private Long blogId;
}
