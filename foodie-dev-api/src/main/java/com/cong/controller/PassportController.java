package com.cong.controller;

import com.cong.pojo.Users;
import com.cong.pojo.bo.ShopcartBO;
import com.cong.pojo.bo.UserBO;
import com.cong.service.UserService;
import com.cong.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redisOperator;

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

        // TODO 生成用户token，存入redis会话
        // TODO 同步购物车数据
        syncShopCartData(userResult.getId(), request, response);

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

        // TODO 生成用户token，存入redis会话
        // TODO 同步购物车数据
        syncShopCartData(userResult.getId(), request, response);

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

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void syncShopCartData(String userId, HttpServletRequest request, HttpServletResponse response) {

        /**
         * 1. redis中无数据，如果cookie中的购物车为空，不做处理
         *                                     不为空，直接放入redis
         * 2.        有                         为空，直接把redis的购物车覆盖本地cookie
         *                                     不为空，如果cookie中的某个商品在redis中，
         *                                     则以cookie为主，删除redis中的，把cookie中的商品直接覆盖redis中
         * 3. 同步redis中去了以后，覆盖本地cookie中购物车的数据，保证本地购物车的数据是同步最新的
         */

        // 从redis中获取购物车
        String key = FOODIE_SHOPCART + ":" + userId;
        String shopCartJsonRedis = redisOperator.get(key);

        // 从cookie中获取购物车
        String shopCartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopCartJsonRedis)) {
            // redis空，cookie不空，直接把cookie中的数据放入redis
            if (StringUtils.isNotBlank(shopCartStrCookie)) {
                redisOperator.set(key, shopCartStrCookie);
            }
        } else {
            // redis不空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖redis）
            if (StringUtils.isNotBlank(shopCartStrCookie)) {
                /**
                 * 1. 已经存在的，把cookie中对应的数量覆盖redis（参考京东）
                 * 2. 该项商品标记为待删除，统一放入一个待删除的list
                 * 3. 从cookie中清理所有的待删除list
                 * 4. 合并redis和cookie中的数据
                 * 5. 更新redis和cookie中
                 */

                List<ShopcartBO> shopCartListRedis = JSONUtils.jsonToList(shopCartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopCartListCookie = JSONUtils.jsonToList(shopCartStrCookie, ShopcartBO.class);

                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopCart : shopCartListRedis) {
                    String redisSpecId = redisShopCart.getSpecId();

                    for (ShopcartBO cookieShopCart : shopCartListCookie) {
                        String cookieSpecId = cookieShopCart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            // 覆盖够买数量，不累加，参考京东
                            redisShopCart.setBuyCounts(cookieShopCart.getBuyCounts());
                            // 把cookieShopCart 放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopCart);
                        }

                    }
                }

                // 从现有cookie中删除对应的覆盖过的商品数据
                shopCartListCookie.removeAll(pendingDeleteList);

                // 合并两个list
                shopCartListRedis.addAll(shopCartListCookie);
                // 更新到redis和cookie中
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JSONUtils.objectToJson(shopCartListRedis), true);
                redisOperator.set(key, JSONUtils.objectToJson(shopCartListRedis));
            } else {
                // redis不空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopCartJsonRedis, true);
            }
        }

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
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);

        return CONGJSONResult.ok();
    }
}
