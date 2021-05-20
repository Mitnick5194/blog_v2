package com.ajie.blog.account.service;

import com.ajie.blog.account.api.dto.*;
import com.ajie.commons.RestResponse;

/**
 * 账号服务
 */
public interface AccountService {

    /**
     * 注册
     *
     * @param dto
     * @return
     */
    Integer register(RegisterReqDto dto);

    /**
     * 检查用户名是否存在
     *
     * @param name
     * @return 0否1是
     */
    Integer checkUserName(String name);

    /**
     * 登录
     *
     * @param dto
     * @return
     */
    String login(LoginReqDto dto);

    /**
     * 退出登录
     *
     * @return
     */
    Integer loginout();

    /**
     * 修改密码
     *
     * @return
     */
    Integer changePassword(ChangePasswordReqDto dto);

    /**
     * 更新用户信息
     *
     * @param dto
     * @return
     */
    Integer updateUserInfo(UpdateUserReqDto dto);


    /**
     * 更新用户名
     *
     * @param accountName
     * @return
     */
    Integer updateAccountName(String accountName);
}
