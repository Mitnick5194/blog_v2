package com.ajie.blog.controller;

import com.ajie.blog.api.dto.CommentDto;
import com.ajie.blog.api.dto.CommentReqDto;
import com.ajie.blog.api.dto.CommentRespDto;
import com.ajie.blog.api.rest.CommentRestApi;
import com.ajie.blog.service.CommentService;
import com.ajie.commons.RestResponse;
import com.ajie.commons.dto.PageDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/micro-blog/v2/comment")
public class CommentController implements CommentRestApi {
    @Resource
    private CommentService commentService;


    @Override
    public RestResponse<Long> createComment(CommentDto dto) {
        return RestResponse.success(commentService.createComment(dto));
    }

    @Override
    public RestResponse<Integer> deleteComment(CommentDto dto) {
        return RestResponse.success(commentService.deleteComment(dto));
    }

    @Override
    public RestResponse<PageDto<CommentRespDto>> queryByBlogId(CommentReqDto dto) {
        return RestResponse.success(commentService.queryByBlogId(dto));
    }
}
