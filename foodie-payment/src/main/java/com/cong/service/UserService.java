package com.cong.service;

import com.cong.pojo.Orders;
import com.cong.pojo.Users;
import com.cong.pojo.bo.MerchantOrdersBO;

public interface UserService {

	/**
	 * @Description: 查询用户信息
	 */
	public Users queryUserInfo(String userId, String pwd);

}

