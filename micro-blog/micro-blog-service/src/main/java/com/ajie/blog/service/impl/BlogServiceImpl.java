package com.ajie.blog.service.impl;

import com.ajie.blog.api.dto.BlogQueryReqDto;
import com.ajie.blog.api.dto.BlogReqDto;
import com.ajie.blog.api.enums.BlogExceptionEmun;
import com.ajie.blog.api.po.BlogPO;
import com.ajie.blog.api.po.BlogTagPO;
import com.ajie.blog.exception.BlogException;
import com.ajie.blog.mapper.BlogMapper;
import com.ajie.blog.service.BlogService;
import com.ajie.commons.constant.TableConstant;
import com.ajie.commons.dto.PageDto;
import com.ajie.commons.utils.PageDtoUtils;
import com.ajie.commons.utils.ParamCheck;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService, TableConstant {
    Logger logger = LoggerFactory.getLogger(BlogServiceImpl.class);
    @Resource
    private BlogMapper mapper;

    @Override
    public Long create(BlogReqDto blog) {
        ParamCheck.assertNull(blog.getTitle(), BlogException.paramError("标题"));
        ParamCheck.assertNull(blog.getContent(), BlogException.paramError("内容"));
        ParamCheck.assertNull(blog.getTagList(), BlogException.paramError("标签"));
        BlogPO po = new BlogPO();
        BeanUtils.copyProperties(blog, po);
        mapper.insert(po);
        return po.getId();
    }

    @Override
    public Integer update(BlogReqDto blog) {
        ParamCheck.assertNull(blog.getId(), BlogException.paramError("文章ID"));
        ParamCheck.assertNull(blog.getTitle(), BlogException.paramError("标题"));
        ParamCheck.assertNull(blog.getContent(), BlogException.paramError("内容"));
        ParamCheck.assertNull(blog.getTagList(), BlogException.paramError("标签"));
        BlogPO po = new BlogPO();
        BeanUtils.copyProperties(blog, po);
        int ret = mapper.updateById(po);
        return ret;
    }

    @Override
    public Integer deleteById(Long id) {
        BlogPO blogPo = mapper.selectById(id);
        if (null == blogPo) {
            throw BlogException.of(BlogExceptionEmun.PARAM_ERROR.getCode(), "找不到文章：" + id);
        }
        BlogPO po = new BlogPO();
        po.setId(id);
        po.setDel(DEL);
        int ret = mapper.updateById(po);
        return ret;
    }

    @Override
    public PageDto<List<BlogReqDto>> queryByPage(BlogQueryReqDto dto) {
        Page<BlogPO> page = new Page<>(dto.getCurrentPage(), dto.getPageSize());
        if (CollectionUtils.isEmpty(dto.getTagList())) {
            IPage<BlogPO> blogPoIPage = mapper.queryByPage(page, dto);
            PageDto<List<BlogReqDto>> result = PageDtoUtils.toPageDto(blogPoIPage);
            //TODO 用户信息
            return result;
        }
        //查询标签中间表
        BlogTagPO blogTag = new BlogTagPO();
        //blogTag.setTagId(dto.gett);
        return null;
    }

    public int migrate() {
        List<BlogPO> blogPOs = mapper.queryOldData();
        List<BlogPO> list = blogPOs.stream().map(s -> {
            BlogPO t = new BlogPO();
            BeanUtils.copyProperties(s, t);
            t.setId(null);
            return t;
        }).collect(Collectors.toList());
        for (BlogPO item : list) {
            mapper.insert(item);
        }
        return blogPOs.size();
    }
}
