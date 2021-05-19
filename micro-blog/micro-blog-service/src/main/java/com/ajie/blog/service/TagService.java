package com.ajie.blog.service;

import com.ajie.blog.api.dto.TagDto;
import com.ajie.commons.dto.BasePageReqDto;
import com.ajie.commons.dto.PageDto;

import java.util.List;

public interface TagService {

    /**
     * 新增标签，已存在不会添加，保存后id会保存到dto
     *
     * @param dto
     * @return
     */
    Integer createTags(List<TagDto> dto);

    /**
     * 分页查询
     *
     * @return
     */
    PageDto<TagDto> queryTagPage(BasePageReqDto dto);
}
