package com.cong.controller;

import com.cong.enums.YesOrNo;
import com.cong.pojo.Carousel;
import com.cong.pojo.Category;
import com.cong.pojo.vo.CategoryVO;
import com.cong.pojo.vo.NewItemsVO;
import com.cong.service.CarouselService;
import com.cong.service.CategoryService;
import com.cong.utils.CONGJSONResult;
import com.cong.utils.JSONUtils;
import com.cong.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public CONGJSONResult carousel() {

        List<Carousel> list;
        String carouselStr = redisOperator.get("carousel");
        if (StringUtils.isBlank(carouselStr)) {
            list = carouselService.queryAll(YesOrNo.YES.type);
            redisOperator.set("carousel", JSONUtils.objectToJson(list));
        } else {
            list = JSONUtils.jsonToList(carouselStr, Carousel.class);
        }

        return CONGJSONResult.ok(list);
    }

    /**
     * 1. 后台运营系统，一旦广告（轮播图）发生更改，就可以删除缓存，然后重置
     * 2. 定时重置，比如每天凌晨三点重置
     * 3. 每个轮播图都有坑是一个广告，每个广告都会有一个过期时间，过期了，再重置
     */

    /**
     * 首页分类展示需求
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标上移到大分类，则加载其子分类的内容，如果已经存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public CONGJSONResult cats() {
        List<Category> list;
        String catsStr = redisOperator.get("cats");
        if (StringUtils.isBlank(catsStr)) {
            list = categoryService.queryAllRootLevelCat();
            redisOperator.set("cats", JSONUtils.objectToJson(list));
        } else {
            list = JSONUtils.jsonToList(catsStr, Category.class);
        }

        return CONGJSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public CONGJSONResult subCat(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {
        if (rootCatId == null) {
            return CONGJSONResult.errorMsg("分类不存在");
        }

        List<CategoryVO> list;
        String key = "subCat:" + rootCatId;
        String subCatStr = redisOperator.get(key);
        if (StringUtils.isBlank(subCatStr)) {
            list = categoryService.getSubCatList(rootCatId);
            redisOperator.set(key, JSONUtils.objectToJson(list));
        } else {
            list = JSONUtils.jsonToList(subCatStr, CategoryVO.class);
        }

        return CONGJSONResult.ok(list);
    }

    @ApiOperation(value = "查询每个一级子分类下的最新6条数据", notes = "查询每个一级子分类下的最新6条数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public CONGJSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {
        if (rootCatId == null) {
            return CONGJSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return CONGJSONResult.ok(list);
    }
}
