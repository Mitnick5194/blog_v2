package com.ajie.blog.service;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogReqDto;
import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.commons.dto.PageDto;

import java.util.List;

public interface BlogService {

    /**
     * 新增
     *
     * @param blog
     * @return
     */
    Long create(BlogReqDto blog);


    /**
     * 更新
     *
     * @param blog
     * @return
     */
    Integer update(BlogReqDto blog);

    /**
     * 保存草稿
     *
     * @param blog
     * @return
     */
    Long saveDraft(BlogReqDto blog);

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    Integer deleteById(Long id);

    /**
     * 分页查询
     *
     * @param dto
     * @return
     */
    PageDto<List<BlogRespDto>> queryByPage(BlogQueryReqDto dto);

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    BlogRespDto queryBlogById(Long id);

    int migrate();
}
