package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.api.dto.*;
import com.ajie.blog.account.api.po.AccountPO;
import com.ajie.blog.account.exception.AccountException;
import com.ajie.blog.account.mapper.AccountMapper;
import com.ajie.blog.account.service.AccountService;
import com.ajie.commons.RestResponse;
import com.ajie.commons.encrypt.EncryptUtil;
import com.ajie.commons.exception.MicroCommonException;
import com.ajie.commons.utils.ParamCheck;
import com.ajie.commons.utils.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class AccountServcieImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;

    @Override
    public Integer register(RegisterReqDto dto) {
        //ParamCheck.assertNull(dto.getAccountName(), MicroCommonException.PARAM_ERROR.paramErrorFiled("用户名"));
        ParamCheck.assertNull(dto.getNickName(), MicroCommonException.PARAM_ERROR.paramErrorFiled("昵称"));
        ParamCheck.assertNull(dto.getPassword(), MicroCommonException.PARAM_ERROR.paramErrorFiled("密码"));
        String accountName = dto.getAccountName();
        if (StringUtils.isBlank(accountName)) {
            //用户名为空，生成随机用户名
            accountName = AccountHelper.genRandomAccountName(accountMapper, 0);
            dto.setAccountName(accountName);
        } else {
            Integer exist = checkUserName(accountName);
            if (Integer.valueOf(1).equals(exist)) {
                throw AccountException.USER_NAME_EXIST;
            }
        }
        AccountPO po = new AccountPO();
        po.setAccountName(dto.getAccountName());
        po.setNickName(dto.getNickName());
        if (StringUtils.isNotBlank(dto.getPhone())) {
            po.setPhone(dto.getPhone());
        }
        if (StringUtils.isNotBlank(dto.getMail())) {
            po.setMail(dto.getMail());
        }
        if (Objects.nonNull(dto.getGender())) {
            po.setGender(dto.getGender());
        }
        if (StringUtils.isNotBlank(dto.getHeaderUrl())) {
            po.setHeaderUrl(dto.getHeaderUrl());
        }
        if (StringUtils.isNotBlank(dto.getPersonalSign())) {
            po.setPersonalSign(dto.getPersonalSign());
        }
        //处理密码，密码加盐（用户名）
        String password = dto.getPassword();
        password += accountName;
        password = EncryptUtil.sha256Encrypt(password);
        dto.setPassword(password);
        //好像自定义sql不会自动生成id，只能手动来了
        long id = IdWorker.getId();
        int ret = accountMapper.register(id, dto);
        if (0 == ret) {
            throw AccountException.USER_NAME_EXIST;
        }
        return ret;
    }


    @Override
    public Integer checkUserName(String name) {
        return null;
    }


    @Override
    public AccountRespDto login(LoginReqDto dto) {
        return null;
    }

    @Override
    public Integer loginout() {
        return null;
    }

    @Override
    public Integer changePassword(ChangePasswordReqDto dto) {
        return null;
    }

    @Override
    public Integer updateUserInfo(UpdateUserReqDto dto) {
        return null;
    }
}
