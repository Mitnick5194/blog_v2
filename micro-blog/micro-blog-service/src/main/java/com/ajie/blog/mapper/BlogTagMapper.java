package com.ajie.blog.mapper;

import com.ajie.blog.api.po.BlogTagPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogTagMapper extends BaseMapper<BlogTagPO> {
}