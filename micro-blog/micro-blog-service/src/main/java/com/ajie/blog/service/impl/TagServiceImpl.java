package com.ajie.blog.service.impl;

import com.ajie.blog.api.dto.TagDto;
import com.ajie.blog.api.enums.BlogExceptionEmun;
import com.ajie.blog.api.po.TagPO;
import com.ajie.blog.exception.BlogException;
import com.ajie.blog.mapper.TagMapper;
import com.ajie.blog.service.TagService;
import com.ajie.commons.dto.BasePageReqDto;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.PageDtoUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service

public class TagServiceImpl implements TagService {
    @Resource
    private TagMapper tagMapper;

    @Override
    public Long createTag(String tagName) {
        if (StringUtils.isBlank(tagName)) {
            throw BlogException.of(BlogExceptionEmun.PARAM_ERROR.getCode(), "标签内容为空");
        }
        TagPO po = new TagPO();
        po.setTagName(tagName);
        TagPO tag = tagMapper.selectOne(po.toQueryWrap());
        if (null != tag) {
            return tag.getId();
        }
        tagMapper.insert(po);
        return po.getId();
    }

    @Override
    public PageDto<TagDto> queryTagPage(BasePageReqDto dto) {
        Page page = new Page(dto.getCurrentPage(), dto.getPageSize());
        IPage<TagPO> list = tagMapper.selectPage(page, new TagPO().toQueryWrap());
        PageDto<TagDto> pageDto = PageDtoUtils.toPageDto(list, (s) -> {
            TagDto t = new TagDto();
            BeanUtils.copyProperties(s, t);
            return t;
        });
        return pageDto;
    }
}
