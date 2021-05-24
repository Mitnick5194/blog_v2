package com.ajie.blog.controller;

import com.ajie.blog.account.api.dto.AccountRespDto;
import com.ajie.blog.account.api.rest.AccountRestApi;
import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogReqDto;
import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.rest.BlogRestApi;
import com.ajie.blog.migrate.MigrateService;
import com.ajie.blog.service.BlogService;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.ApiUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
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

    @Resource
    private AccountRestApi accountRestApi;

    @Resource
    private MigrateService migrateService;

    @Override
    public RestResponse<Long> create(BlogReqDto blog) {
        Long data = blogService.create(blog);
        return RestResponse.success(data);
    }

    @Override
    public RestResponse<Integer> update(BlogReqDto blog) {
        return RestResponse.success(blogService.update(blog));
    }

    @Override
    public RestResponse<Long> saveDraft(BlogReqDto blog) {
        return RestResponse.success(blogService.saveDraft(blog));
    }

    @Override
    public RestResponse<Integer> deleteById(@RequestParam("id") Long id) {
        return RestResponse.success(blogService.deleteById(id));
    }

    @Override
    public RestResponse<PageDto<List<BlogRespDto>>> queryByPage(@RequestBody BlogQueryReqDto dto) {
        return RestResponse.success(blogService.queryByPage(dto));
    }

    @Override
    public RestResponse<BlogRespDto> queryBlogById(Long id) {
       return RestResponse.success(blogService.queryBlogById(id));
    }

    @GetMapping("migrate")
    public RestResponse<Integer> migrate(@RequestParam(required = false, name = "userId") String userId) {
        return RestResponse.success(migrateService.migrate(userId));
    }

    @GetMapping("test")
    public void test() {
        //1395288739512655873
        RestResponse<List<AccountRespDto>> data = accountRestApi.queryAccountInfo(Collections.singletonList(1395288739512655873L));
        List<AccountRespDto> accountRespDtos = ApiUtil.checkAndGetData(data);
        //RestResponse<AccountRespDto> data = accountRestApi.test();
        System.out.println(data);
    }
}
