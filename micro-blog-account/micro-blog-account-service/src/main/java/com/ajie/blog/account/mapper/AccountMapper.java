package com.ajie.blog.account.mapper;

import com.ajie.blog.account.api.dto.RegisterReqDto;
import com.ajie.blog.account.api.po.AccountPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper extends BaseMapper<AccountPO> {
    @Insert({"<script>",
            "insert into mb_account (account_name,nick_name,password,mail,phone,gender,personal_sign,header_url) ",
            "select #{dto.accountName},#{dto.nickName},#{dto.password},#{dto.mail},#{dto.phone},#{dto.gender},#{dto.personalSign},#{dto.header_url} ",
            "from DUAL ",
            "where not exists(select account_name from mb_account where mb_account = #{dto.accountName} )",
            "</script>"})
    int register(@Param("dto") RegisterReqDto dto);
}