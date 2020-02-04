package com.cong.controller;

import com.cong.pojo.Users;
import com.cong.pojo.bo.UserBO;
import com.cong.service.UserService;
import com.cong.utils.CONGJSONResult;
import com.cong.utils.CookieUtils;
import com.cong.utils.JSONUtils;
import com.cong.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public CONGJSONResult usernameIsExist(@RequestParam String username) {

        // 1. 判断用户名不能为空
        if (StringUtils.isBlank(username)) {
            return CONGJSONResult.errorMsg("用户名不能为空");
        }

        // 2. 查找注册用户是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return CONGJSONResult.errorMsg("用户名已存在");
        }

        // 3. 请求成功，用户名没有重复
        return CONGJSONResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/registry")
    public CONGJSONResult registry(@RequestBody UserBO userBO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        // 0. 判断用户名和密码是否为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password) ||
                StringUtils.isBlank(confirmPassword)) {
            return CONGJSONResult.errorMsg("用户名或密码为空");
        }

        // 1. 查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return CONGJSONResult.errorMsg("用户名已存在");
        }

        // 2. 密码长度不能少于6位
        if (password.length() < 6) {
            return CONGJSONResult.errorMsg("密码长度不能少于6位");
        }

        // 3. 判断两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return CONGJSONResult.errorMsg("两次密码输入不一致");
        }

        // 4. 实现注册
        Users userResult = userService.createUser(userBO);
        setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user",
                JSONUtils.objectToJson(userResult), true);

        return CONGJSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public CONGJSONResult login(@RequestBody UserBO userBO,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 0. 判断用户名和密码是否为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return CONGJSONResult.errorMsg("用户名或密码为空");
        }

        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));
        if (userResult == null) {
            return CONGJSONResult.errorMsg("用户名或密码不正确");
        }

        setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user",
                JSONUtils.objectToJson(userResult), true);

        return CONGJSONResult.ok(userResult);
    }

    private Users setNullProperty(Users user) {
        user.setPassword(null);
        user.setRealname(null);
        user.setBirthday(null);
        user.setEmail(null);
        user.setUpdatedTime(null);
        user.setCreatedTime(null);
        return user;
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public CONGJSONResult logout(@RequestParam String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        // 清除用户的相关信息的 Cookie
        CookieUtils.deleteCookie(request, response, "user");

        // TODO 用户退出登录，需要清空购物车
        // TODO 分布式会话中需要清除用户数据

        return CONGJSONResult.ok();
    }
}
