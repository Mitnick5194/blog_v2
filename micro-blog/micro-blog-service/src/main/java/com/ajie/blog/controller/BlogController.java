package com.ajie.blog.controller;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogReqDto;
import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.rest.BlogRestApi;
import com.ajie.blog.service.BlogService;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.PageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 博文请求入口
 */
@Api(tags = "博文模块")
@RequestMapping("/micro-blog/v2/blog")
@RestController
public class BlogController implements BlogRestApi {

    @Resource
    private BlogService blogService;

    @ApiOperation(value = "新增博文", notes = "新增博文")
    @PostMapping("/create")
    @Override
    public RestResponse<Long> create(BlogReqDto blog) {
        Long data = blogService.create(blog);
        return RestResponse.success(data);
    }

    @ApiOperation(value = "更新博文", notes = "更新博文")
    @PostMapping("/update")
    @Override
    public RestResponse<Integer> update(BlogReqDto blog) {
        return RestResponse.success(blogService.update(blog));
    }

    @Override
    public RestResponse<Long> saveDraft(BlogReqDto blog) {
        return RestResponse.success(blogService.saveDraft(blog));
    }

    @ApiOperation(value = "根据ID删除", notes = "根据ID删除")
    @GetMapping("/delete-by-id")
    @Override
    public RestResponse<Integer> deleteById(@RequestParam("id") Long id) {
        return RestResponse.success(blogService.deleteById(id));
    }

    @ApiOperation(value = "分页查询", notes = "分页查询")
    @PostMapping("/query-by-page")
    @Override
    public RestResponse<PageDto<List<BlogRespDto>>> queryByPage(@RequestBody BlogQueryReqDto dto) {
        return RestResponse.success(blogService.queryByPage(dto));
    }

    @Override
    public RestResponse<BlogRespDto> queryBlogById(Long id) {
        return RestResponse.success(blogService.queryBlogById(id));
    }

    @GetMapping("migrate")
    public RestResponse<Integer> migrate() {
        return RestResponse.success(blogService.migrate());
    }
}
