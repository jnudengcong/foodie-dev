package com.cong.controller;

import com.cong.pojo.UserAddress;
import com.cong.service.AddressService;
import com.cong.utils.CONGJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "收货地址相关", tags = {"收货地址相关的api接口"})
@RestController
@RequestMapping("address")
public class AddressController {

    /**
     * 用户在确认订单页面，可以针对收货地址做如下操作：
     * 1. 查询用户的所有收货地址列表
     * 2. 新增收货地址
     * 3. 删除收货地址
     * 4. 修改收货地址
     * 5. 设置默认地址
     */

    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表", httpMethod = "GET")
    @GetMapping("/list")
    public CONGJSONResult list(@RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return CONGJSONResult.errorMsg("");
        }

        List<UserAddress> list = addressService.queryAll(userId);

        return CONGJSONResult.ok(list);
    }
}
