package com.ajie.blog.service.impl;

import com.ajie.blog.api.dto.CommentDto;
import com.ajie.blog.api.dto.CommentReqDto;
import com.ajie.blog.api.dto.CommentRespDto;
import com.ajie.blog.api.po.CommentPO;
import com.ajie.blog.exception.BlogException;
import com.ajie.blog.mapper.CommentMapper;
import com.ajie.blog.service.CommentService;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.PageDtoUtil;
import com.ajie.commons.utils.ParamCheck;
import com.ajie.commons.utils.UserInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;

    @Override
    public Long createComment(CommentDto dto) {
        ParamCheck.assertNull(dto.getBlogId(), BlogException.paramError("文章ID"));
        ParamCheck.assertNull(dto.getContent(), BlogException.paramError("内容"));
        CommentPO po = new CommentPO();
        BeanUtils.copyProperties(dto, po);
        po.setUserId(UserInfoUtil.getUserId());
        return po.getId();
    }

    @Override
    public Integer deleteComment(CommentDto dto) {
        Long id = dto.getId();
        ParamCheck.assertNull(id, BlogException.paramError("评论ID"));
        return commentMapper.deleteById(id);
    }

    @Override
    public PageDto<CommentRespDto> queryByBlogId(CommentReqDto dto) {
        Long blogId = dto.getBlogId();
        if (null == blogId) {
            return PageDto.empty();
        }
        Page page = new Page(dto.getCurrentPage(), dto.getPageSize());
        CommentPO po = new CommentPO();
        po.setBlogId(blogId);
        QueryWrapper<CommentPO> wrap = po.toQueryWrap();
        wrap.isNull("parent_id");
        IPage<CommentPO> p = commentMapper.selectPage(page, wrap);
        List<CommentPO> records = p.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageDto.empty();
        }
        List<CommentRespDto> list = records.stream().map(s -> {
            CommentRespDto t = new CommentRespDto();
            BeanUtils.copyProperties(s, t);
            return t;
        }).collect(Collectors.toList());
        fillChildrenComment(list);
        return PageDtoUtil.toPageDto(p, list);
    }

    /**
     * 填充子评论
     */
    private void fillChildrenComment(List<CommentRespDto> list) {
        List<Long> ids = list.stream().map(CommentRespDto::getId).collect(Collectors.toList());
        CommentPO po = new CommentPO();
        QueryWrapper<CommentPO> wrap = po.toQueryWrap();
        wrap.in("parent_id", StringUtils.join(ids, ","));
        List<CommentPO> children = commentMapper.selectList(wrap);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        Map<Long, List<CommentPO>> map = children.stream().collect(Collectors.groupingBy(CommentPO::getParentId));
        for (CommentRespDto item : list) {
            List<CommentPO> pos = map.get(item.getId());
            if (CollectionUtils.isEmpty(pos)) {
                continue;
            }
            List<CommentRespDto> c = pos.stream().map(s -> {
                CommentRespDto t = new CommentRespDto();
                BeanUtils.copyProperties(s, t);
                return t;
            }).collect(Collectors.toList());
            item.setChildren(c);
        }
    }
}
