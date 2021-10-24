package com.bjpowernode.service;

import com.bjpowernode.entity.Income;
import com.bjpowernode.entity.ext.UserIncome;

import java.util.List;

//收益服务
public interface IncomeService {

    //获取用户的所有收益记录
    List<UserIncome> queryIncomeListUid(Integer uid, Integer pageNo,Integer pageSize);

    //获取用户的收益记录总数
    int queryIncomeCountUid(Integer uid);

    //计算满标产品的收益计划
    void generateIncomePlan();

    //收益返还
    void generateIncomeBack();

}
