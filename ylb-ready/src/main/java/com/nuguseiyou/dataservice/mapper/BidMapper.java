package com.nuguseiyou.dataservice.mapper;

import com.nuguseiyou.model.Bid;

public interface BidMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Bid record);

    int insertSelective(Bid record);

    Bid selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Bid record);

    int updateByPrimaryKey(Bid record);
}