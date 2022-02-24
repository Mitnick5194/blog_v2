package com.ajie.blog.account.mapper;

import com.ajie.blog.account.api.po.OauthAccountPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OauthMapper extends BaseMapper<OauthAccountPO> {
}
