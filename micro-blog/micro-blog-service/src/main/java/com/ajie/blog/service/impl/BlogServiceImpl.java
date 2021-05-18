package com.ajie.blog.service.impl;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogReqDto;
import com.ajie.blog.api.dto.BlogRespDto;
import com.ajie.blog.api.enums.BlogExceptionEmun;
import com.ajie.blog.api.po.BlogPO;
import com.ajie.blog.api.po.BlogTagPO;
import com.ajie.blog.api.po.DraftBlogPO;
import com.ajie.blog.exception.BlogException;
import com.ajie.blog.mapper.BlogMapper;
import com.ajie.blog.mapper.BlogTagMapper;
import com.ajie.blog.mapper.DraftBlogMapper;
import com.ajie.blog.service.BlogService;
import com.ajie.commons.constant.TableConstant;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.PageDtoUtils;
import com.ajie.commons.utils.ParamCheck;
import com.ajie.commons.utils.UserInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService, TableConstant {
    Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);
    @Resource
    private BlogMapper blogMapper;
    @Resource
    private DraftBlogMapper draftBlogMapper;
    @Resource
    private BlogTagMapper blogTagMapper;

    @Override
    public Long create(BlogReqDto dto) {
        ParamCheck.assertNull(dto.getTitle(), BlogException.paramError("标题"));
        ParamCheck.assertNull(dto.getContent(), BlogException.paramError("内容"));
        ParamCheck.assertNull(dto.getTagList(), BlogException.paramError("标签"));
        BlogPO po = new BlogPO();
        BeanUtils.copyProperties(dto, po);
        po.setUserId(UserInfoUtil.getUserId());
        blogMapper.insert(po);
        return po.getId();
    }

    @Override
    public Integer update(BlogReqDto dto) {
        ParamCheck.assertNull(dto.getId(), BlogException.paramError("文章ID"));
        ParamCheck.assertNull(dto.getTitle(), BlogException.paramError("标题"));
        ParamCheck.assertNull(dto.getContent(), BlogException.paramError("内容"));
        ParamCheck.assertNull(dto.getTagList(), BlogException.paramError("标签"));
        BlogPO po = new BlogPO();
        BeanUtils.copyProperties(dto, po);
        int ret = blogMapper.updateById(po);
        return ret;
    }

    @Override
    public Long saveDraft(BlogReqDto blog) {
        DraftBlogPO po = new DraftBlogPO();
        Long id = blog.getId();
        if (null == id) {
            //新建草稿
            BeanUtils.copyProperties(blog, po);
            draftBlogMapper.insert(po);
            return po.getId();
        }
        //查询是否有该草稿
        po.setRefBlogId(blog.getId());
        DraftBlogPO draft = draftBlogMapper.selectOne(po.toQueryWrap());
        if (null != draft) {
            //有了直接更新
            BeanUtils.copyProperties(blog, draft);
            draftBlogMapper.updateById(draft);
            return draft.getId();
        }
        //没有，新建
        BeanUtils.copyProperties(blog, po);
        draftBlogMapper.insert(po);
        return po.getId();
    }

    @Override
    public Integer deleteById(Long id) {
        BlogPO blogPo = blogMapper.selectById(id);
        if (null == blogPo) {
            throw BlogException.of(BlogExceptionEmun.PARAM_ERROR.getCode(), "找不到文章：" + id);
        }
        BlogPO po = new BlogPO();
        po.setId(id);
        po.setDel(DEL);
        int ret = blogMapper.updateById(po);
        return ret;
    }

    @Override
    public PageDto<List<BlogRespDto>> queryByPage(BlogQueryReqDto dto) {
        BlogPO po = new BlogPO();
        po.setUserId(4234234L);
        List<BlogPO> blogPOS = blogMapper.selectList(po.toQueryWrap());
        System.out.println(blogPOS.size());
        Page<BlogPO> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        List<Long> blogIds = null;
        if (CollectionUtils.isNotEmpty(dto.getTagList())) {
            //查询标签中间表
            BlogTagPO blogTag = new BlogTagPO();
            QueryWrapper<BlogTagPO> wrap = new QueryWrapper<>();
            wrap.in("tag_id", StringUtils.join(dto.getTagList(), ","));
            List<BlogTagPO> btp = blogTagMapper.selectList(wrap);
            blogIds = btp.stream().map(BlogTagPO::getBlogId).collect(Collectors.toList());
        }
        IPage<BlogRespDto> blogPoPage = blogMapper.queryByPage(page, dto, blogIds, 324134234L);
        PageDto<List<BlogRespDto>> result = PageDtoUtils.toPageDto(blogPoPage);
        //TODO 用户信息
        return result;
    }

    @Override
    public BlogRespDto queryBlogById(Long id) {
        BlogPO blogPO = blogMapper.selectById(id);
        if (null == blogPO) {
            return null;
        }
        BlogRespDto dto = new BlogRespDto();
        dto.build(blogPO);
        return dto;
    }

    public int migrate() {
        List<BlogPO> blogPOs = blogMapper.queryOldData();
        List<BlogPO> list = blogPOs.stream().map(s -> {
            BlogPO t = new BlogPO();
            BeanUtils.copyProperties(s, t);
            t.setId(null);
            return t;
        }).collect(Collectors.toList());
        for (BlogPO item : list) {
            blogMapper.insert(item);
        }
        return blogPOs.size();
    }

    public static void main(String[] args) {
        BlogPO po = new BlogPO();
        po.setId(1L);
        po.setTitle("abc");
        po.setType(2);
        QueryWrapper wrap = po.toQueryWrap();
        System.out.println(wrap);

        QueryWrapper wrap2 = new QueryWrapper();
        wrap2.eq("id", 234234l);
        System.out.println(wrap2);
    }
}
