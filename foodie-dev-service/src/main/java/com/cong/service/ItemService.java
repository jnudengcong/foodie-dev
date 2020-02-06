package com.cong.service;

import com.cong.pojo.Items;
import com.cong.pojo.ItemsImg;
import com.cong.pojo.ItemsParam;
import com.cong.pojo.ItemsSpec;
import com.cong.pojo.vo.CommentLevelCountsVO;
import com.cong.utils.PagedGridResult;

import java.util.List;

public interface ItemService {

    /**
     * 根据商品ID查询详情
     * @param itemId
     * @return
     */
    public Items queryItemById(String itemId);

    /**
     * 根据商品ID查询商品图片列表
     * @param itemId
     * @return
     */
    public List<ItemsImg> queryItemImgList(String itemId);

    /**
     * 根据商品ID查询商品规格
     * @param itemId
     * @return
     */
    public List<ItemsSpec> queryItemSpecList(String itemId);

    /**
     * 根据商品ID查询商品参数
     * @param itemId
     * @return
     */
    public ItemsParam queryItemParam(String itemId);

    /**
     * 根据商品id查询商品的评价等级数量
     * @param itemId
     */
    public CommentLevelCountsVO queryCommentCounts(String itemId);

    /**
     * 根据商品id查询商品的评价 (分页)
     * @param itemId
     * @param level
     * @return
     */
    public PagedGridResult queryPagedComments(String itemId,
                                              Integer level,
                                              Integer page,
                                              Integer pageSize);

    /**
     * 搜索商品列表
     * @param keywords
     * @param sort
     * @param page
     * @param pageSize
     * @return
     */
    public PagedGridResult searchItems(String keywords, String sort,
                                       Integer page, Integer pageSize);
}
