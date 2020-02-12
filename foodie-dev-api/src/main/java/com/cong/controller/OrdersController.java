package com.cong.controller;

import com.cong.pojo.bo.SubmitOrderBO;
import com.cong.utils.CONGJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "收货地址相关", tags = {"收货地址相关的api接口"})
@RestController
@RequestMapping("orders")
public class OrdersController {

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public CONGJSONResult create(@RequestBody SubmitOrderBO submitOrderBO) {

        System.out.println(submitOrderBO.toString());

        // 1. 创建订单
        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据

        return CONGJSONResult.ok();
    }
}
