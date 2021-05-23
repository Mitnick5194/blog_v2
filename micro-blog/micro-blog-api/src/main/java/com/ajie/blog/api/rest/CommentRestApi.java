package com.ajie.blog.api.rest;

import com.ajie.blog.api.dto.CommentDto;
import com.ajie.blog.api.dto.CommentReqDto;
import com.ajie.blog.api.dto.CommentRespDto;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.PageDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("micro-blog")
@Api(tags = "评论模块")
@RequestMapping("/micro-blog/v2/comment")
public interface CommentRestApi {

    /**
     * 新增评论
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "新增评论", notes = "新增评论")
    @PostMapping("/createComment")
    RestResponse<Long> createComment(@RequestBody CommentDto dto);

    /**
     * 删除评论
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "删除评论", notes = "删除评论")
    @PostMapping("/deleteComment")
    RestResponse<Integer> deleteComment(@RequestBody CommentDto dto);

    /**
     * 根据博文ID分页查询
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "根据博文ID分页查询", notes = "根据博文ID分页查询")
    @PostMapping("/queryByBlogId")
    RestResponse<PageDto<CommentRespDto>> queryByBlogId(@RequestBody CommentReqDto dto);
}
