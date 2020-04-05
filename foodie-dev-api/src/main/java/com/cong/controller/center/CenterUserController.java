package com.cong.controller.center;

import com.cong.pojo.Users;
import com.cong.pojo.bo.center.CenterUserBO;
import com.cong.service.center.CenterUserService;
import com.cong.utils.CONGJSONResult;
import com.cong.utils.CookieUtils;
import com.cong.utils.JSONUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "用户信息接口", tags = {"用户信息相关接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "修改用户信息", notes = "修改用户信息", httpMethod = "POST")
    @PostMapping("/update")
    public CONGJSONResult update(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @RequestBody CenterUserBO centerUserBO,
            HttpServletRequest request, HttpServletResponse response) {

        Users userResult = centerUserService.updateUserInfo(userId, centerUserBO);

        userResult = setNullProperty(userResult);
        CookieUtils.setCookie(request, response, "user", JSONUtils.objectToJson(userResult), true);

        // TODO 后续要改，增加令牌token，会整合redis，分布式会话

        return CONGJSONResult.ok();
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

}
