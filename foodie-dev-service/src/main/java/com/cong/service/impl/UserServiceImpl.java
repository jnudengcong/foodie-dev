package com.cong.service.impl;

import com.cong.enums.Sex;
import com.cong.mapper.UsersMapper;
import com.cong.pojo.Users;
import com.cong.pojo.bo.UserBO;
import com.cong.service.UserService;
import com.cong.utils.DateUtil;
import com.cong.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    private static final String USER_FACE = "http://m.imeitou.com/uploads/allimg/2019062018/md1gpgcmzq5.jpg";
    private static final String BIRTHDAY = "1900-01-01";

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {

        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("username", username);

        Users result = usersMapper.selectOneByExample(userExample);

        return result != null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users createUser(UserBO userBO) {

        Users user = new Users();

        String userID = sid.nextShort();
        user.setID(userID);

        user.setUsername(userBO.getUsername());

        try {
            user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 默认使用用户名作为用户昵称
        user.setNickname(userBO.getUsername());

        // 默认头像
        user.setFace(USER_FACE);

        // 默认生日
        user.setBirthday(DateUtil.stringToDate(BIRTHDAY));

        // 默认性别为 保密
        user.setSex(Sex.secret.type);

        Date date = new Date();
        user.setCreatedTime(date);
        user.setUpdatedTime(date);

        usersMapper.insert(user);

        return user;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {

        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();

        userCriteria.andEqualTo("username", username);
        userCriteria.andEqualTo("password", password);

        Users result = usersMapper.selectOneByExample(userExample);

        return result;
    }
}
