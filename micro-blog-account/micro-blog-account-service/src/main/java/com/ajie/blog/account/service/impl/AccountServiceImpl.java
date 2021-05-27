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
import com.ajie.commons.utils.*;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    @Resource
    private AccountMapper accountMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Integer register(RegisterReqDto dto) {
        //ParamCheck.assertNull(dto.getAccountName(), MicroCommonException.PARAM_ERROR.paramErrorFiled("用户名"));
        /* ParamCheck.assertNull(dto.getNickName(), MicroCommonException.PARAM_ERROR.paramErrorFiled("昵称"));*/
        ParamCheck.assertNull(dto.getPassword(), MicroCommonException.PARAM_ERROR.paramErrorFiled("密码"));
        ParamCheck.assertNull(dto.getVerifyCode(), MicroCommonException.PARAM_ERROR.paramErrorFiled("验证码"));
        String key = dto.getKey();
        String s = stringRedisTemplate.opsForValue().get(key);
        String verifyCode = dto.getVerifyCode();
        if (null == s || !s.equalsIgnoreCase(verifyCode)) {
            throw new AccountException(MicroCommonException.PARAM_ERROR.getCode(), "验证码错误");
        }
        AccountPO po = new AccountPO();
        BeanUtils.copyProperties(dto, po);
        String headerUrl = dto.getHeaderUrl();
        if (StringUtils.isBlank(headerUrl)) {
            headerUrl = Properties.defaultUserHeader;
            po.setHeaderUrl(headerUrl);
        }
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
    public LoginRespDto login(LoginReqDto dto) {
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
        LoginRespDto resp = new LoginRespDto();
        BeanUtils.copyProperties(account, resp);
        resp.setPhone(AccountHelper.mask(account.getPhone()));
        resp.setMail(AccountHelper.mask(account.getMail()));
        resp.setToken(token);
        return resp;
    }

    @Override
    public Integer logout() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String auth = request.getHeader("auth");
        if (StringUtils.isBlank(auth)) {
            return 0;
        }
        try {
            JwtAccount account = JwtUtil.verifyToken(auth, Properties.tokenSecret);
            if (null == account) {
                return 0;
            }
            Date expire = account.getExpire();
            LocalDateTime localDateTime = DateUtil.date2LocalDateTime(expire);
            Duration between = Duration.between( DateUtil.date2LocalDateTime(new Date()),localDateTime);
            long sec = between.getSeconds();//秒
            if (0 >= sec) {
                //过期了
                return 0;
            }
            stringRedisTemplate.opsForValue().set(account.getSign(), "1", sec, TimeUnit.SECONDS);
            return 1;
        } catch (TokenExpiredException e) {
            //已经过期了
        }
        return 0;
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
    public Integer updateAccountName(UpdateAccountNameReqDto dto) {
        String password = dto.getPassword();
        String accountName = dto.getAccountName();
        ParamCheck.assertNull(password, MicroCommonException.PARAM_ERROR.paramErrorFiled("登录账号为空"));
        ParamCheck.assertNull(accountName, MicroCommonException.PARAM_ERROR.paramErrorFiled("用户名"));
        AccountPO accountPO = getLoginAccount();
        if (null == accountPO) {
            throw AccountException.USER_NAME_EXIST;
        }
        if (accountName.equals(accountPO.getAccountName())) {
            throw new AccountException(AccountException.USER_VERIFY_ERROR.getCode(), "新用户名不能和旧用户名一样");
        }
        Integer autoAccountName = accountPO.getAutoAccountName();
        if (!Integer.valueOf(1).equals(autoAccountName)) {
            throw new AccountException(AccountException.USER_VERIFY_ERROR.getCode(), "用户名不可修改");
        }
        //验证密码
        String oldPassword = AccountHelper.encryptPassword(password, accountName);
        if (!oldPassword.equals(accountPO.getPassword())) {
            throw new AccountException(MicroCommonException.PARAM_ERROR.getCode(), "密码错误");
        }
        //处理密码
        String newPassword = AccountHelper.encryptPassword(password, accountName);
        accountPO.setAccountName(dto.getAccountName());
        accountPO.setPassword(newPassword);
        return accountMapper.updateById(accountPO);
    }

    @Override
    public List<AccountRespDto> queryAccountInfo(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        AccountPO po = new AccountPO();
        QueryWrapper<AccountPO> wrap = po.wrap(AccountPO.class);
        wrap.in("id", ids.toArray());
        List<AccountPO> accounts = accountMapper.selectList(wrap);
        //List<AccountPO> accounts = accountMapper.queryList(ids);
        if (CollectionUtils.isEmpty(accounts)) {
            return Collections.emptyList();
        }
        return accounts.stream().map(s -> {
            AccountRespDto t = new AccountRespDto();
            BeanUtils.copyProperties(s, t);
            return t;
        }).collect(Collectors.toList());
    }

    public VerifyCodeRestDto getVerifyCode() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String code = VerifyCodeUtil.drawImage(out);
        //转成base64
        String encodeCode = Base64.encodeBase64String(out.toByteArray());
        String key = RandomUtil.getRandomString_36();
        VerifyCodeRestDto dto = new VerifyCodeRestDto();
        dto.setKey(key);
        dto.setVerifyCode(encodeCode);
        //将key放入redis,5分钟有效
        stringRedisTemplate.opsForValue().set(key, code, 300, TimeUnit.SECONDS);
        return dto;
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
