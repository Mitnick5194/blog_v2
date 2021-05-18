package com.ajie.blog.service;

import com.ajie.blog.api.dto.CommentDto;
import com.ajie.blog.api.dto.CommentReqDto;
import com.ajie.blog.api.dto.CommentRespDto;
import com.ajie.commons.dto.PageDto;

/**
 * 评论
 */
public interface CommentService {

    /**
     * 新增评论
     *
     * @param dto
     * @return
     */
    Long createComment(CommentDto dto);

    /**
     * 删除评论
     *
     * @param dto
     * @return
     */
    Integer deleteComment(CommentDto dto);

    /**
     * 根据博文ID分页查询
     *
     * @param dto
     * @return
     */
    PageDto<CommentRespDto> queryByBlogId(CommentReqDto dto);
}
