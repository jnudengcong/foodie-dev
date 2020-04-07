package com.cong.controller.center;

import com.cong.controller.BaseController;
import com.cong.service.center.MyOrdersService;
import com.cong.utils.CONGJSONResult;
import com.cong.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Api(value = "用户中心我的订单", tags = {"用户中心我的订单"})
@RestController
@RequestMapping("myorders")
public class MyOrdersController extends BaseController {

    @Autowired
    private MyOrdersService myOrdersService;

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/query")
    public CONGJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderStatus", value = "订单状态", required = true)
            @RequestParam Integer orderStatus,
            @ApiParam(name = "page", value = "查询页数", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "查询每页的记录数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return CONGJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult grid = myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);

        return CONGJSONResult.ok(grid);
    }

    // 商家发货没有后端，所以这个接口仅仅只是用于模拟
    @ApiOperation(value = "商家发货", notes = "商家发货", httpMethod = "GET")
    @GetMapping("/deliver")
    public CONGJSONResult deliver(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) throws Exception {

        if (StringUtils.isBlank(orderId)) {
            return CONGJSONResult.errorMsg("订单ID不能为空");
        }
        myOrdersService.updateDeliverOrderStatus(orderId);
        return CONGJSONResult.ok();
    }

    @ApiOperation(value = "用户确认收货", notes = "用户确认收货", httpMethod = "POST")
    @PostMapping("/confirmReceive")
    public CONGJSONResult confirmReceive(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) throws Exception {

        CONGJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrdersService.updateReceiveOrderStatus(orderId);
        if (!res) {
            return CONGJSONResult.errorMsg("订单确认收货失败！");
        }

        return CONGJSONResult.ok();
    }

    /**
     * 用于验证用户和订单是否有联系，避免非法用户调用
     * @return
     */
//    private CONGJSONResult checkUserOrder(String userId, String orderId) {
//        Orders order = myOrdersService.queryMyOrder(userId, orderId);
//        if (order == null) {
//            return CONGJSONResult.errorMsg("订单不存在！");
//        }
//
//        return CONGJSONResult.ok();
//    }

    @ApiOperation(value = "用户删除订单", notes = "用户删除订单", httpMethod = "POST")
    @PostMapping("/delete")
    public CONGJSONResult delete(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) throws Exception {

        CONGJSONResult checkResult = checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }

        boolean res = myOrdersService.deleteOrder(userId, orderId);
        if (!res) {
            return CONGJSONResult.errorMsg("订单删除失败！");
        }

        return CONGJSONResult.ok();
    }
}
