package com.cong.controller;

import com.cong.enums.OrderStatusEnum;
import com.cong.enums.PayMethod;
import com.cong.pojo.OrderStatus;
import com.cong.pojo.bo.ShopcartBO;
import com.cong.pojo.bo.SubmitOrderBO;
import com.cong.pojo.vo.MerchantOrdersVO;
import com.cong.pojo.vo.OrderVO;
import com.cong.service.OrderService;
import com.cong.utils.CONGJSONResult;
import com.cong.utils.CookieUtils;
import com.cong.utils.JSONUtils;
import com.cong.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "收货地址相关", tags = {"收货地址相关的api接口"})
@RestController
@RequestMapping("orders")
public class OrdersController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public CONGJSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        Integer payMethod = submitOrderBO.getPayMethod();
        if (!payMethod.equals(PayMethod.WEIXIN.type) && !payMethod.equals(PayMethod.ALIPAY.type)) {
            return CONGJSONResult.errorMsg("支付方式不支持！");
        }

//        System.out.println(submitOrderBO.toString());

        String key = FOODIE_SHOPCART + ":" + submitOrderBO.getUserId();
        String shopCartJson = redisOperator.get(key);
        if (StringUtils.isBlank(shopCartJson)) {
            return CONGJSONResult.errorMsg("购物数据不正确");
        }

        List<ShopcartBO> shopCartBOList = JSONUtils.jsonToList(shopCartJson, ShopcartBO.class);

        // 1. 创建订单
        OrderVO orderVO = orderService.createOrder(shopCartBOList, submitOrderBO);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后，移除购物车中已结算（已提交）的商品
        shopCartBOList.removeAll(orderVO.getToBeRemoveShopCartList());
        redisOperator.set(key, JSONUtils.objectToJson(shopCartBOList));
        // 整合redis之后，完善购物车中的已结算商品清除，并且同步到前端的cookie
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JSONUtils.objectToJson(shopCartBOList), true);

        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        // 为了方便测试，所有支付的金额都改为1分钱
        merchantOrdersVO.setAmount(1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        ResponseEntity<CONGJSONResult> responseEntity = restTemplate.postForEntity(paymentUrl,
                                                                                    entity,
                                                                                    CONGJSONResult.class);

        CONGJSONResult paymentResult = responseEntity.getBody();
        if (paymentResult.getStatus() != HttpStatus.OK.value()) {
            return CONGJSONResult.errorMsg("支付中心订单创建失败，请联系管理员！");
        }

        return CONGJSONResult.ok(orderId);
    }

    @PostMapping("notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId) {

        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);

        return HttpStatus.OK.value();
    }

    @PostMapping("getPaidOrderInfo")
    public CONGJSONResult getPaidOrderInfo(String orderId) {
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return CONGJSONResult.ok(orderStatus);
    }
}
