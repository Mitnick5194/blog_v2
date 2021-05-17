package com.ajie.blog.api.rest;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogReqDto;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.PageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*@FeignClient*/
@Api(tags = "博文模块")
@RequestMapping("/v2/blog")
public interface BlogRestApi {

    /**
     * 新增
     *
     * @param blog
     * @return
     */
    @ApiOperation(value = "新增博文", notes = "新增博文")
    @PostMapping("/create")
    RestResponse<Long> create(@RequestBody BlogReqDto blog);


    /**
     * 更新
     *
     * @param blog
     * @return
     */
    @ApiOperation(value = "更新博文", notes = "更新博文")
    @PostMapping("/update")
    RestResponse<Integer> update(@RequestBody BlogReqDto blog);

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
    RestResponse<PageDto<List<BlogReqDto>>> queryByPage(@RequestBody BlogQueryReqDto dto);


}
