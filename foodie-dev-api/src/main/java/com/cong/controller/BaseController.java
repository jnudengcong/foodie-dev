package com.cong.controller;

import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 10;

    // 支付中心的调用地址
    public static final String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    // 微信支付 -> 支付中心 -> 电商平台
    //                    |->    回调通知的url
    public static final String payReturnUrl = "http://localhost:8088/orders/notifyMerchantOrderPaid";
}
