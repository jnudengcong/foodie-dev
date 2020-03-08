package com.cong.service;

import com.cong.pojo.bo.SubmitOrderBO;

public interface OrderService {

    /**
     * 用于创建订单相关信息
     * @param submitOrderBO
     */
    public String createOrder(SubmitOrderBO submitOrderBO);

    /**
     * 修改订单状态
     * @param orderId
     * @param orderStatus
     */
    public void updateOrderStatus(String orderId, Integer orderStatus);
}
