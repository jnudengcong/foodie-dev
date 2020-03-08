package com.cong.controller;

import com.cong.enums.OrderStatusEnum;
import com.cong.enums.PayMethod;
import com.cong.pojo.bo.SubmitOrderBO;
import com.cong.service.OrderService;
import com.cong.utils.CONGJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "收货地址相关", tags = {"收货地址相关的api接口"})
@RestController
@RequestMapping("orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public CONGJSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        Integer payMethod = submitOrderBO.getPayMethod();
        if (!payMethod.equals(PayMethod.WEIXIN.type) && !payMethod.equals(PayMethod.ALIPAY.type)) {
            return CONGJSONResult.errorMsg("支付方式不支持！");
        }

        System.out.println(submitOrderBO.toString());

        // 1. 创建订单
        String orderId = orderService.createOrder(submitOrderBO);

        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        // TODO 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
//        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, "", true);

        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据

        return CONGJSONResult.ok(orderId);
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {

        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);

        return HttpStatus.OK.value();
    }
}
