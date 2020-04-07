package com.cong.mapper;

import com.cong.my.mapper.MyMapper;
import com.cong.pojo.ItemsComments;

import java.util.Map;

public interface ItemsCommentsMapperCustom extends MyMapper<ItemsComments> {

    public void saveComments(Map<String, Object> map);

}