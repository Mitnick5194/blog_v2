package com.ajie.blog.mapper;

import com.ajie.blog.api.po.DraftBlogPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DraftBlogMapper extends BaseMapper<DraftBlogPO> {
}