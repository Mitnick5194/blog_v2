package com.ajie.blog.api.rest;

import com.ajie.blog.api.dto.TagDto;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.BasePageReqDto;
import com.ajie.commons.dto.PageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("micro-blog")
@Api(tags = "标签模块")
@RequestMapping("/micro-blog/v2/tag")
public interface TagRestApi {
    /**
     * 新增标签，已存在不会添加，保存后id会保存到dto
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "新增标签，已存在不会添加，保存后id会保存到dto", notes = "新增标签，已存在不会添加，保存后id会保存到dto")
    @PostMapping("/create")
    RestResponse<Integer> createTags(@RequestBody List<TagDto> dto);

    /**
     * 分页查询
     *
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @PostMapping("/query-by-page")
    RestResponse<PageDto<TagDto>> queryTagPage(@RequestBody BasePageReqDto dto);
}
