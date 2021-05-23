package com.ajie.blog.account.service;

import com.ajie.blog.account.api.dto.*;
import com.ajie.commons.RestResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

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
    LoginRespDto login(LoginReqDto dto);

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
     * @param dto
     * @return
     */
    Integer updateAccountName(UpdateAccountNameReqDto dto);

    /**
     * 根据ID查询用户列表信息
     *
     * @param ids
     * @return
     */
    List<AccountRespDto> queryAccountInfo(List<Long> ids);
}
