package com.cong.service;

import com.cong.pojo.UserAddress;

import java.util.List;

public interface AddressService {

    /**
     * 根据用户id查询用户所有收货地址列表
     * @param userId
     * @return
     */
    public List<UserAddress> queryAll(String userId);

}
