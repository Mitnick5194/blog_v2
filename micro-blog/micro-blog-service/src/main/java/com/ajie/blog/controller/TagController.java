package com.ajie.blog.controller;

import com.ajie.blog.api.dto.TagDto;
import com.ajie.blog.api.rest.TagRestApi;
import com.ajie.blog.service.TagService;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.BasePageReqDto;
import com.ajie.commons.dto.PageDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/micro-blog/v2/tag")
public class TagController implements TagRestApi {
    @Resource
    private TagService tagService;

    @Override
    public RestResponse<Integer> createTags(List<TagDto> dto) {
        return RestResponse.success(tagService.createTags(dto));
    }

    @Override
    public RestResponse<PageDto<TagDto>> queryTagPage(BasePageReqDto dto) {
        return RestResponse.success(tagService.queryTagPage(dto));
    }
}
