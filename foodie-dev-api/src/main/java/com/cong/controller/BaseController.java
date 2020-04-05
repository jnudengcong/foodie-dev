package com.cong.controller;

import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMENT_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 10;

    // 支付中心的调用地址
    public static final String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    // 微信支付 -> 支付中心 -> 电商平台
    //                    |->    回调通知的url
    public static final String payReturnUrl = "http://2n2jtf.natappfree.cc/orders/notifyMerchantOrderPaid";

    // 用户上传头像的位置
    public static final String IMAGE_USER_FACE_LOCATION = "C:" + File.separator + "javaProject" +
                                                                File.separator + "workspaces" +
                                                                File.separator + "images" +
                                                                File.separator + "foodie" +
                                                                File.separator + "faces";
}
