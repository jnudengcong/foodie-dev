package com.cong.service.center;

import com.cong.utils.PagedGridResult;

public interface MyOrdersService {

    /**
     * 查询我的订单列表
     * @param userId
     * @param orderStatus
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult queryMyOrders(String userId, Integer orderStatus, Integer page, Integer pageSize);

    /**
     * 订单状态 -> 商家发货
     * @param orderId
     */
    public void updateDeliverOrderStatus(String orderId);
}
