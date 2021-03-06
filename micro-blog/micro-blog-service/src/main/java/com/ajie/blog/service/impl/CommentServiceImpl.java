package com.ajie.blog.service.impl;

import com.ajie.blog.account.api.dto.AccountRespDto;
import com.ajie.blog.account.api.rest.AccountRestApi;
import com.ajie.blog.api.constant.BlogConstant;
import com.ajie.blog.api.dto.CommentDto;
import com.ajie.blog.api.dto.CommentReqDto;
import com.ajie.blog.api.dto.CommentRespDto;
import com.ajie.blog.api.po.CommentPO;
import com.ajie.blog.exception.BlogException;
import com.ajie.blog.mapper.CommentMapper;
import com.ajie.blog.service.CommentService;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.ApiUtil;
import com.ajie.commons.utils.PageDtoUtil;
import com.ajie.commons.utils.ParamCheck;
import com.ajie.commons.utils.UserInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService, BlogConstant {
    private Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private AccountRestApi accountRestApi;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Long createComment(CommentDto dto) {
        ParamCheck.assertNull(dto.getBlogId(), BlogException.paramError("文章ID"));
        ParamCheck.assertNull(dto.getContent(), BlogException.paramError("内容"));
        CommentPO po = new CommentPO();
        BeanUtils.copyProperties(dto, po);
        po.setUserId(UserInfoUtil.getUserId());
        commentMapper.insert(po);
        try {
            //评论数+1
            stringRedisTemplate.opsForHash().increment(COMMENT_COUNT_KEY, String.valueOf(dto.getBlogId()), 1L);
        } catch (Exception e) {
            logger.warn("设置评论数失败", e);
        }

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
        QueryWrapper<CommentPO> wrap = po.wrap(CommentPO.class);
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
        fillAccountInfo(list);
        return PageDtoUtil.toPageDto(p, list);
    }

    /**
     * 填充子评论
     */
    private void fillChildrenComment(List<CommentRespDto> list) {
        List<Long> ids = list.stream().map(CommentRespDto::getId).collect(Collectors.toList());
        CommentPO po = new CommentPO();
        QueryWrapper<CommentPO> wrap = po.wrap(CommentPO.class);
        wrap.in("parent_id", ids.toArray());
        List<CommentPO> children = commentMapper.selectList(wrap);
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        Map<Long, List<CommentPO>> map = children.stream().collect(Collectors.groupingBy(CommentPO::getParentId));
        List<CommentRespDto> all = new ArrayList<>();
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
            all.addAll(c);
        }
        fillAccountInfo(all);
    }

    private void fillAccountInfo(List<CommentRespDto> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }
        List<Long> userIds = records.stream().map(CommentRespDto::getUserId).collect(Collectors.toList());
        List<AccountRespDto> accountList = ApiUtil.checkAndGetData(accountRestApi.queryAccountInfo(userIds));
        if (CollectionUtils.isNotEmpty(accountList)) {
            Map<Long, AccountRespDto> map = accountList.stream().collect(Collectors.toMap(AccountRespDto::getId, Function.identity()));
            for (CommentRespDto item : records) {
                AccountRespDto account = map.get(item.getUserId());
                if (null == account) {
                    continue;
                }
                item.setUserHeaderUrl(account.getHeaderUrl());
                item.setUserName(account.getAccountName());
                item.setUserId(account.getId());
            }
        }
    }
}
