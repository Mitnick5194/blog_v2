package com.ajie.blog.account.service.impl;

import com.ajie.blog.account.api.dto.*;
import com.ajie.blog.account.api.po.AccountPO;
import com.ajie.blog.account.config.Properties;
import com.ajie.blog.account.exception.AccountException;
import com.ajie.blog.account.mapper.AccountMapper;
import com.ajie.blog.account.service.AccountService;
import com.ajie.commons.constant.TableConstant;
import com.ajie.commons.dto.JwtAccount;
import com.ajie.commons.exception.MicroCommonException;
import com.ajie.commons.utils.JwtUtil;
import com.ajie.commons.utils.ParamCheck;
import com.ajie.commons.utils.UserInfoUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServcieImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;

    @Override
    public Integer register(RegisterReqDto dto) {
        //ParamCheck.assertNull(dto.getAccountName(), MicroCommonException.PARAM_ERROR.paramErrorFiled("用户名"));
        ParamCheck.assertNull(dto.getNickName(), MicroCommonException.PARAM_ERROR.paramErrorFiled("昵称"));
        ParamCheck.assertNull(dto.getPassword(), MicroCommonException.PARAM_ERROR.paramErrorFiled("密码"));
        AccountPO po = new AccountPO();
        BeanUtils.copyProperties(dto, po);
        String accountName = dto.getAccountName();
        if (StringUtils.isBlank(accountName)) {
            //用户名为空，生成随机用户名
            accountName = AccountHelper.genRandomAccountName(accountMapper, 0);
            po.setAccountName(accountName);
            po.setAutoAccountName(TableConstant.BOOLEAN_TRUE);
        } else {
            //检验合法性
            boolean check = AccountHelper.checkAccountName(accountName);
            if (!check) {
                throw AccountException.USER_ILLEGAL_NAME;
            }
            Integer exist = checkUserName(accountName);
            if (Integer.valueOf(1).equals(exist)) {
                throw AccountException.USER_NAME_EXIST;
            }
        }

        //处理密码，密码加盐（用户名）
        String password = dto.getPassword();
        password = AccountHelper.encryptPassword(password, accountName);
        po.setPassword(password);
        //好像自定义sql不会自动生成id，只能手动来了
        long id = IdWorker.getId();
        int ret = accountMapper.register(id, po);
        if (0 == ret) {
            throw AccountException.USER_NAME_EXIST;
        }
        return ret;
    }


    @Override
    public Integer checkUserName(String name) {
        AccountPO po = new AccountPO();
        po.setAccountName(name);
        AccountPO account = accountMapper.selectOne(po.wrap(AccountPO.class));
        if (null != account) {
            return 1;
        }
        return 0;
    }


    @Override
    public String login(LoginReqDto dto) {
        ParamCheck.assertNull(dto.getUser(), MicroCommonException.PARAM_ERROR.paramErrorFiled("登录账号为空"));
        ParamCheck.assertNull(dto.getPassword(), MicroCommonException.PARAM_ERROR.paramErrorFiled("密码为空"));
        //分析用户登录类型
        String user = dto.getUser();
        int type = AccountHelper.analyseUser(user);
        AccountPO query = new AccountPO();
        switch (type) {
            case 2:
                query.setPhone(user);
                break;
            case 3:
                query.setMail(user);
                break;
            default:
                query.setAccountName(user);
        }
        AccountPO account = accountMapper.selectOne(query.wrap(AccountPO.class));
        if (null == account) {
            throw AccountException.LOGIN_FAIL;
        }
        String password = account.getPassword();
        //匹配密码
        String paramPassword = AccountHelper.encryptPassword(dto.getPassword(), account.getAccountName());
        if (!password.equals(paramPassword)) {
            throw AccountException.LOGIN_FAIL;
        }
        //创建token
        JwtAccount jwtAccount = new JwtAccount();
        BeanUtils.copyProperties(account, jwtAccount);
        String token = JwtUtil.createToken(Properties.tokenSecret, jwtAccount);
        return token;
    }

    @Override
    public Integer loginout() {
        //TODO 将token加入黑名单
        return null;
    }

    @Override
    public Integer changePassword(ChangePasswordReqDto dto) {
        String newPassword = dto.getNewPassword();
        String oldPassword = dto.getOldPassword();
        AccountPO accountPO = getLoginAccount();
        if (null == accountPO) {
            throw AccountException.USER_NAME_EXIST;
        }
        String password = accountPO.getPassword();
        String handlePassword = AccountHelper.encryptPassword(oldPassword, accountPO.getAccountName());
        if (!handlePassword.equals(password)) {
            throw new AccountException(MicroCommonException.PARAM_ERROR.getCode(), "原密码错误");
        }
        //对新密码进行处理
        String handleNewPassword = AccountHelper.encryptPassword(newPassword, accountPO.getAccountName());
        AccountPO account = new AccountPO();
        account.setPassword(handleNewPassword);
        account.setId(accountPO.getId());
        return accountMapper.updateById(account);
    }

    @Override
    public Integer updateUserInfo(UpdateUserReqDto dto) {
        AccountPO accountPO = getLoginAccount();
        if (null == accountPO) {
            throw AccountException.USER_NAME_EXIST;
        }
        BeanUtils.copyProperties(dto, accountPO);
        return accountMapper.updateById(accountPO);
    }

    @Override
    public Integer updateAccountName(String accountName) {
        if (StringUtils.isBlank(accountName)) {
            throw MicroCommonException.PARAM_ERROR.paramErrorFiled("用户名");
        }
        AccountPO accountPO = getLoginAccount();
        if (null == accountPO) {
            throw AccountException.USER_NAME_EXIST;
        }
        Integer autoAccountName = accountPO.getAutoAccountName();
        if (!Integer.valueOf(1).equals(autoAccountName)) {
            throw new AccountException(AccountException.USER_VERIFY_ERROR.getCode(), "用户名不可修改");
        }
        accountPO.setAccountName(accountName);
        return accountMapper.updateById(accountPO);
    }

    @Override
    public List<AccountRespDto> queryAccountInfo(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        AccountPO po = new AccountPO();
        QueryWrapper<AccountPO> wrap = po.wrap(AccountPO.class);
        wrap.in("id", StringUtils.join(ids, ","));
        List<AccountPO> accounts = accountMapper.selectList(wrap);
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }
        return accounts.stream().map(s -> {
            AccountRespDto t = new AccountRespDto();
            BeanUtils.copyProperties(s, t);
            return t;
        }).collect(Collectors.toList());
    }

    /**
     * 获取当前登录者
     *
     * @return
     */
    private AccountPO getLoginAccount() {
        Long userId = UserInfoUtil.getUserId();
        return accountMapper.selectById(userId);
    }
}
