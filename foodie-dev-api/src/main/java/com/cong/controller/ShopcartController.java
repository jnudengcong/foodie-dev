package com.cong.controller;

import com.cong.pojo.bo.ShopcartBO;
import com.cong.utils.CONGJSONResult;
import com.cong.utils.JSONUtils;
import com.cong.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RestController
@RequestMapping("shopcart")
public class ShopcartController extends BaseController {

    @Autowired
    private RedisOperator redisOperator;

    private final static Logger logger = LoggerFactory.getLogger(ShopcartController.class);

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public CONGJSONResult add(
            @RequestParam String userId,
            @RequestBody ShopcartBO shopcartBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (StringUtils.isBlank(userId)) {
            return CONGJSONResult.errorMsg("");
        }

        logger.info(shopcartBO.toString());

        // 前端用户在登录的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        // 需要额外判断当前购物车中包含已经存在的商品，如果存在则累加购买数量
        String shopCartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        List<ShopcartBO> shopCartList = null;
        if (StringUtils.isNotBlank(shopCartJson)) {
            // redis已经有购物车了
            shopCartList = JSONUtils.jsonToList(shopCartJson, ShopcartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话counts累加
            boolean isHaving = false;
            for (ShopcartBO sc : shopCartList) {
                String tempSpecId = sc.getSpecId();
                if (tempSpecId.equals(shopcartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                }
            }
            if (!isHaving) {
                shopCartList.add(shopcartBO);
            }
        } else {
            shopCartList = new ArrayList<>();
            shopCartList.add(shopcartBO);
        }

        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JSONUtils.objectToJson(shopCartList));

        return CONGJSONResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public CONGJSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return CONGJSONResult.errorMsg("参数不能为空");
        }

        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除 redis 购物车中的商品
        String key = FOODIE_SHOPCART + ":" + userId;
        String shopCartJson = redisOperator.get(key);

        if (StringUtils.isNotBlank(shopCartJson)) {
            // redis 中有该数据
            List<ShopcartBO> shopCartBOList = JSONUtils.jsonToList(shopCartJson, ShopcartBO.class);
            for (ShopcartBO sc : shopCartBOList) {
                if (itemSpecId.equals(sc.getSpecId())) {
                    shopCartBOList.remove(sc);
                    break;
                }
            }
            redisOperator.set(key, JSONUtils.objectToJson(shopCartBOList));
        }

        return CONGJSONResult.ok();
    }
}
