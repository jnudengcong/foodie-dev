package com.cong.controller;

import com.cong.pojo.UserAddress;
import com.cong.pojo.bo.AddressBO;
import com.cong.service.AddressService;
import com.cong.utils.CONGJSONResult;
import com.cong.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "收货地址相关", tags = {"收货地址相关的api接口"})
@RestController
@RequestMapping("address")
public class AddressController {

    /**
     * 用户在确认订单页面，可以针对收货地址做如下操作：
     * 1. 查询用户的所有收货地址列表
     * 2. 新增收货地址
     * 3. 删除收货地址
     * 4. 修改收货地址
     * 5. 设置默认地址
     */

    @Autowired
    private AddressService addressService;

    @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表", httpMethod = "POST")
    @PostMapping("/list")
    public CONGJSONResult list(@RequestParam String userId) {

        if (StringUtils.isBlank(userId)) {
            return CONGJSONResult.errorMsg("");
        }

        List<UserAddress> list = addressService.queryAll(userId);

        return CONGJSONResult.ok(list);
    }

    @ApiOperation(value = "用户新增地址", notes = "用户新增地址", httpMethod = "POST")
    @PostMapping("/add")
    public CONGJSONResult add(@RequestBody AddressBO addressBO) {

        CONGJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200) {
            return checkRes;
        }

        addressService.addNewUserAddress(addressBO);

        return CONGJSONResult.ok();
    }

    private CONGJSONResult checkAddress(AddressBO addressBO) {

        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return CONGJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return CONGJSONResult.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return CONGJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return CONGJSONResult.errorMsg("收货人手机号长度不正确");
        }

        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return CONGJSONResult.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) || StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) || StringUtils.isBlank(detail)) {
            return CONGJSONResult.errorMsg("收货人信息不能为空");
        }

        return CONGJSONResult.ok();
    }
}
