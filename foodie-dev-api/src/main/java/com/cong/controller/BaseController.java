package com.cong.controller;

import com.cong.pojo.Orders;
import com.cong.service.center.MyOrdersService;
import com.cong.utils.CONGJSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;

@Controller
public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMON_PAGE_SIZE = 10;

    public static final Integer PAGE_SIZE = 10;

    // 支付中心的调用地址
    public static final String paymentUrl = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    // 微信支付 -> 支付中心 -> 电商平台
    //                    |->    回调通知的url
    public static final String payReturnUrl = "http://m9qaj8.natappfree.cc/orders/notifyMerchantOrderPaid";

    // 用户上传头像的位置
    public static final String IMAGE_USER_FACE_LOCATION = "C:" + File.separator + "javaProject" +
                                                                File.separator + "workspaces" +
                                                                File.separator + "images" +
                                                                File.separator + "foodie" +
                                                                File.separator + "faces";

    @Autowired
    public MyOrdersService myOrdersService;

    /**
     * 用于验证用户和订单是否有联系，避免非法用户调用
     * @return
     */
    public CONGJSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return CONGJSONResult.errorMsg("订单不存在！");
        }

        return CONGJSONResult.ok(order);
    }
}
