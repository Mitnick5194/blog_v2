package com.ajie.blog.service;

import com.ajie.blog.api.dto.TagDto;
import com.ajie.commons.dto.BasePageReqDto;
import com.ajie.commons.dto.PageDto;

public interface TagService {

    /**
     * 新增标签，已存在不会添加
     *
     * @param tagName
     * @return
     */
    Long createTag(String tagName);

    /**
     * 分页查询
     *
     * @return
     */
    PageDto<TagDto> queryTagPage(BasePageReqDto dto);
}
