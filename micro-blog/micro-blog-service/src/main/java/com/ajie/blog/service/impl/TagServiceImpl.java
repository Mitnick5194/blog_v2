package com.ajie.blog.service.impl;

import com.ajie.blog.api.dto.TagDto;
import com.ajie.blog.api.enums.BlogExceptionEmun;
import com.ajie.blog.api.po.BlogTagPO;
import com.ajie.blog.api.po.TagPO;
import com.ajie.blog.exception.BlogException;
import com.ajie.blog.mapper.BlogTagMapper;
import com.ajie.blog.mapper.TagMapper;
import com.ajie.blog.service.TagService;
import com.ajie.commons.dto.BasePageReqDto;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.PageDtoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {
    @Resource
    private TagMapper tagMapper;

    @Resource
    private BlogTagMapper blogTagMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer createTags(List<TagDto> dto) {
        if (CollectionUtils.isEmpty(dto)) {
            throw BlogException.of(BlogExceptionEmun.PARAM_ERROR.getCode(), "标签内容为空");
        }
        List<Long> ids = dto.stream().map(TagDto::getId).collect(Collectors.toList());
        TagPO po = new TagPO();
        QueryWrapper<TagPO> wrap = po.wrap(TagPO.class);
        wrap.in("id", StringUtils.join(ids, ","));
        List<TagPO> tags = tagMapper.selectList(wrap);
        Map<String, TagPO> map = tags.stream().collect(Collectors.toMap(TagPO::getTagName, Function.identity()));
        int ret = 0;
        for (TagDto t : dto) {
            TagPO tagPO = map.get(t.getTag());
            if (null != tagPO) {
                t.setId(tagPO.getId());
                continue;
            }
            po = new TagPO();
            po.setTagName(t.getTag());
            tagMapper.insert(po);
            t.setId(po.getId());
            ret++;
        }
        return ret;
    }

    @Override
    public PageDto<TagDto> queryTagPage(BasePageReqDto dto) {
        Page page = new Page(dto.getCurrentPage(), dto.getPageSize());
        IPage<TagDto> query = blogTagMapper.queryTag(page);
        PageDto resp = PageDtoUtil.toPageDto(query);
        List<TagDto> records = query.getRecords();
        return resp;
    }
}
