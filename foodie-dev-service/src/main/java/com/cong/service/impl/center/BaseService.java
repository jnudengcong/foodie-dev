package com.cong.service.impl.center;

import com.cong.utils.PagedGridResult;
import com.github.pagehelper.PageInfo;

import java.util.List;

public class BaseService {

    public PagedGridResult setterPagedGrid(List<?> list, Integer page) {
        PageInfo<?> pageList = new PageInfo<>(list);
        PagedGridResult grid = new PagedGridResult();
        grid.setPage(page); // 当前页数
        grid.setRows(list); // 当前页展示的列表
        grid.setTotal(pageList.getPages()); // 总页数
        grid.setRecords(pageList.getTotal()); // 总记录数

        return grid;
    }

}
