package com.cong.controller;

import com.cong.pojo.Items;
import com.cong.pojo.ItemsImg;
import com.cong.pojo.ItemsParam;
import com.cong.pojo.ItemsSpec;
import com.cong.pojo.vo.CommentLevelCountsVO;
import com.cong.pojo.vo.ItemInfoVO;
import com.cong.pojo.vo.ShopcartVO;
import com.cong.service.ItemService;
import com.cong.utils.CONGJSONResult;
import com.cong.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "商品接口", tags = {"商品信息展示的相关接口"})
@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemService itemService;

    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public CONGJSONResult info(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @PathVariable String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return CONGJSONResult.errorMsg(null);
        }

        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemsImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemsSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemsParam = itemService.queryItemParam(itemId);

        ItemInfoVO itemInfoVO = new ItemInfoVO();
        itemInfoVO.setItem(item);
        itemInfoVO.setItemImgList(itemsImgList);
        itemInfoVO.setItemSpecList(itemsSpecList);
        itemInfoVO.setItemParams(itemsParam);

        return CONGJSONResult.ok(itemInfoVO);
    }

    @ApiOperation(value = "查询商品评价等级", notes = "查询商品评价等级", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public CONGJSONResult commentLevel(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId) {

        if (StringUtils.isBlank(itemId)) {
            return CONGJSONResult.errorMsg(null);
        }

        CommentLevelCountsVO countsVO = itemService.queryCommentCounts(itemId);

        return CONGJSONResult.ok(countsVO);
    }

    @ApiOperation(value = "查询商品评论列表", notes = "查询商品评论列表", httpMethod = "GET")
    @GetMapping("/comments")
    public CONGJSONResult comments(
            @ApiParam(name = "itemId", value = "商品id", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level", value = "商品评价等级", required = false)
            @RequestParam Integer level,
            @ApiParam(name = "page", value = "查询页数", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "查询每页的记录数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(itemId)) {
            return CONGJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = BaseController.COMMENT_PAGE_SIZE;
        }

        PagedGridResult grid = itemService.queryPagedComments(itemId, level, page, pageSize);

        return CONGJSONResult.ok(grid);
    }

    @ApiOperation(value = "搜索商品列表", notes = "搜索商品列表", httpMethod = "GET")
    @GetMapping("/search")
    public CONGJSONResult search(
            @ApiParam(name = "keywords", value = "关键字", required = true)
            @RequestParam String keywords,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询页数", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "查询每页的记录数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return CONGJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = BaseController.PAGE_SIZE;
        }

        PagedGridResult grid = itemService.searchItems(keywords, sort, page, pageSize);

        return CONGJSONResult.ok(grid);
    }

    @ApiOperation(value = "根据分类id搜索商品列表", notes = "根据分类id搜索商品列表", httpMethod = "GET")
    @GetMapping("/catItems")
    public CONGJSONResult catItems(
            @ApiParam(name = "catId", value = "关键字", required = true)
            @RequestParam Integer catId,
            @ApiParam(name = "sort", value = "排序", required = false)
            @RequestParam String sort,
            @ApiParam(name = "page", value = "查询页数", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "查询每页的记录数", required = false)
            @RequestParam Integer pageSize) {

        if (catId == null) {
            return CONGJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = BaseController.PAGE_SIZE;
        }

        PagedGridResult grid = itemService.searchItems(catId, sort, page, pageSize);

        return CONGJSONResult.ok(grid);
    }

    // 用于用户长时间未登录网站，刷新购物车中的数据（主要是商品价格）
    @ApiOperation(value = "根据商品规格ids查找最新的商品数据", notes = "根据商品规格ids查找最新的商品数据", httpMethod = "GET")
    @GetMapping("/refresh")
    public CONGJSONResult refresh(
            @ApiParam(name = "itemSpecIds", value = "关键字", required = true)
            @RequestParam String itemSpecIds) {

        if (itemSpecIds == null) {
            return CONGJSONResult.ok();
        }

        List<ShopcartVO> list = itemService.queryItemsBySpecIds(itemSpecIds);

        return CONGJSONResult.ok(list);
    }
}
