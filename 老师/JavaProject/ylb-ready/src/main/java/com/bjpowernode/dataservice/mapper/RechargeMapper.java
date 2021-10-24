package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.entity.Recharge;

public interface RechargeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Recharge record);

    int insertSelective(Recharge record);

    Recharge selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Recharge record);

    int updateByPrimaryKey(Recharge record);
}