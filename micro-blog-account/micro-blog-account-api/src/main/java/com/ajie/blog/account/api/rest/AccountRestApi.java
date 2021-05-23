package com.ajie.blog.account.api.rest;

import com.ajie.blog.account.api.dto.*;
import com.ajie.commons.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "micro-blog-account")
@Api(tags = "用户模块")
@RequestMapping("/micro-blog/v2/account")
public interface AccountRestApi {

    /**
     * 注册
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "注册", notes = "注册")
    @PostMapping("/register")
    RestResponse<Integer> register(@RequestBody RegisterReqDto dto);

    /**
     * 登录
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "登录", notes = "登录")
    @PostMapping("/login")
    RestResponse<LoginRespDto> login(@RequestBody LoginReqDto dto);

    /**
     * 退出登录
     *
     * @return
     */
    @ApiOperation(value = "退出登录", notes = "退出登录")
    @GetMapping("/login-out")
    RestResponse<Integer> loginout();

    /**
     * 修改密码
     *
     * @return
     */
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PostMapping("/change-password")
    RestResponse<Integer> changePassword(@RequestBody ChangePasswordReqDto dto);

    /**
     * 更新用户信息
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "更新用户信息", notes = "更新用户信息")
    @PostMapping("/update-user-info")
    RestResponse<Integer> updateUserInfo(@RequestBody UpdateUserReqDto dto);


    /**
     * 更新用户名
     *
     * @param dto
     * @return
     */
    @ApiOperation(value = "更新用户名", notes = "更新用户名")
    @PostMapping("/update-account-name")
    RestResponse<Integer> updateAccountName(@RequestBody UpdateAccountNameReqDto dto);

    /**
     * 根据ID查询用户列表信息
     *
     * @param ids
     * @return
     */
    @ApiOperation(value = "根据ID查询用户列表信息", notes = "根据ID查询用户列表信息")
    @PostMapping("/query-account-info")
    RestResponse<List<AccountRespDto>> queryAccountInfo(@RequestBody List<Long> ids);

    /**
     * 根据ID查询用户列表信息
     *
     * @param =
     * @return
     */
    @ApiOperation(value = "根据ID查询用户列表信息", notes = "根据ID查询用户列表信息")
    @GetMapping("/hello")
    RestResponse<AccountRespDto> test();


}
