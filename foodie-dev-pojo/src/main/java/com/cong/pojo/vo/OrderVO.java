package com.cong.pojo.vo;

import com.cong.pojo.bo.ShopcartBO;

import java.util.List;

public class OrderVO {

    private String orderId;
    private MerchantOrdersVO merchantOrdersVO;
    private List<ShopcartBO> toBeRemoveShopCartList;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public MerchantOrdersVO getMerchantOrdersVO() {
        return merchantOrdersVO;
    }

    public void setMerchantOrdersVO(MerchantOrdersVO merchantOrdersVO) {
        this.merchantOrdersVO = merchantOrdersVO;
    }

    public List<ShopcartBO> getToBeRemoveShopCartList() {
        return toBeRemoveShopCartList;
    }

    public void setToBeRemoveShopCartList(List<ShopcartBO> toBeRemoveShopCartList) {
        this.toBeRemoveShopCartList = toBeRemoveShopCartList;
    }
}
