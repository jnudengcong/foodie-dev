package com.cong.service;

import com.cong.pojo.Users;
import com.cong.pojo.bo.UserBO;

public interface UserService {
    /**
     * 判断用户名是否存在
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 创建用户
     */
    public Users createUser(UserBO userBO);

    /**
     * 检索用户名以及密码是否匹配，用于登录
     */
    public Users queryUserForLogin(String username, String password);
}
