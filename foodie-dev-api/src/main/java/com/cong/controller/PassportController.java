package com.cong.controller;

import com.cong.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @GetMapping("/usernameIsExist")
    public int usernameIsExist(@RequestParam String username) {

        // 1. 判断用户名不能为空
        if (StringUtils.isBlank(username)) {
            return 500;
        }

        // 2. 查找注册用户是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return 500;
        }

        // 3. 请求成功，用户名没有重复
        return 200;
    }
}
