package com.nuguseiyou.service;

import com.nuguseiyou.model.ext.UserIncome;

import java.util.List;

public interface IncomeService {
    /**
     * 查询用户最近收益记录
     *
     * @param uid      用户id
     * @param pageNo   页码
     * @param pageSize 页面数据大小
     * @return 返回用户最近收益记录集合
     */
    List<UserIncome> queryUserIncomeRecord(Integer uid, Integer pageNo, Integer pageSize);


    /**
     * 查询用户最近收益记录数
     *
     * @param uid 用户id
     * @return 返回用户最近收益记录数
     */
    int queryUserIncomeRecordCount(Integer uid);


    //已满标产品生成收益计划
    void queryFullProduct();

    //到期收益返还
    void incomeReturn();


}
