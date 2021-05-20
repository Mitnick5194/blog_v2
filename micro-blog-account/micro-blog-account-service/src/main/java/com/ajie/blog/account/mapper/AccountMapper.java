package com.ajie.blog.account.mapper;

import com.ajie.blog.account.api.po.AccountPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper extends BaseMapper<AccountPO> {
    @Insert({"<script>",
            "insert into mb_account (id,account_name,nick_name,password,mail,phone,gender,personal_sign,header_url,create_time,update_time,del) ",
            "select #{id}, #{po.accountName},#{po.nickName},#{po.password},#{po.mail},#{po.phone},#{po.gender},#{po.personalSign},#{po.headerUrl},now(),now(),0 ",
            "from DUAL ",
            "where not exists(select account_name from mb_account where account_name = #{po.accountName} )",
            "</script>"})
    int register(@Param("id") Long id, @Param("po") AccountPO po);
}