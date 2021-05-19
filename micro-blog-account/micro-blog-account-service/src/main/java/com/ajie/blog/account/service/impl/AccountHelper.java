package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.api.po.AccountPO;
import com.ajie.blog.account.exception.AccountException;
import com.ajie.blog.account.mapper.AccountMapper;
import com.ajie.commons.utils.RandomUtil;

public class AccountHelper {
    /**
     * 生成随机用户名,用户名冲突会重复调用该方法5次
     *
     * @param count 当前重复次数
     * @return
     */
    protected static String genRandomAccountName(AccountMapper mapper, int count) {
        if (count == 5) {
            throw AccountException.USER_REGISTER_FAIL;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("u_");
        String r = RandomUtil.getRandomString(16, true);
        sb.append(r);
        String name = sb.toString();
        //查询名字是否已存在
        AccountPO po = new AccountPO();
        po.setAccountName(name);
        AccountPO exist = mapper.selectOne(po.wrap(AccountPO.class));
        if (null == exist) {
            return name;
        }
        //重试
        return genRandomAccountName(mapper, ++count);
    }
}
