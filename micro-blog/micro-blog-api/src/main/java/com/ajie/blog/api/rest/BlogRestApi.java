package com.ajie.blog.api.rest;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogReqDto;
import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.dto.DraftBlogReqDto;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.PageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("micro-blog")
@Api(tags = "博文模块")
@RequestMapping("/micro-blog/v2/blog")
public interface BlogRestApi {

    /**
     * 新增
     *
     * @param blog
     * @return
     */
    @ApiOperation(value = "新增博文", notes = "新增博文")
    @PostMapping("/save")
    RestResponse<Long> save(@RequestBody BlogReqDto blog);

    /**
     * 保存草稿
     *
     * @param blog
     * @return
     */
    @ApiOperation(value = "保存草稿", notes = "更新博文")
    @PostMapping("/save-draft")
    RestResponse<Long> saveDraft(@RequestBody DraftBlogReqDto blog);

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除", notes = "根据ID删除")
    @GetMapping("/delete-by-id")
    RestResponse<Integer> deleteById(@RequestParam("id") Long id);

    /**
     * 分页查询
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "根据ID删除", notes = "根据ID删除")
    @PostMapping("/query-by-page")
    RestResponse<PageDto<List<BlogRespDto>>> queryByPage(@RequestBody BlogQueryReqDto dto);

    @ApiOperation(value = "根据ID查询", notes = "根据ID查询")
    @GetMapping("/query-by-id")
    RestResponse<BlogRespDto> queryBlogById(@RequestParam("id") Long id);

    @ApiOperation(value = "设为私有/设为公开", notes = "设为私有/设为公开")
    @GetMapping("/toggle-private")
    RestResponse<Integer> togglePrivate(@RequestParam("id") Long id, @RequestParam("type") Integer type);


}
