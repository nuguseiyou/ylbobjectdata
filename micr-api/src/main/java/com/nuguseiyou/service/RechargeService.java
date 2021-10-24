package com.nuguseiyou.service;

import com.nuguseiyou.model.Recharge;
import com.nuguseiyou.vo.Result;

import java.util.List;

public interface RechargeService {

    /**
     *                  查询用户最近充值记录
     * @param uid       用户id
     * @param pageNo    页码
     * @param pageSize  页面数据大小
     * @return          返回用户最近充值记录集合
     */
    List<Recharge> queryUserRechargeRecord(Integer uid, Integer pageNo, Integer pageSize);

    /**
     * @param uid 用户id
     * @return    返回用户最近充值记录数
     */
    int queryUserRechargeRecordCount(Integer uid);

    //创建用户充值记录
    int addRecharge(Recharge recharge);

    //异步充值通知
    Result AsyncRechargeNotify(String orderId, String payResult, String payAmount);

    //验签失败 修改充值状态为3  验签失败
    boolean editRechargeStatus(String orderId, int signatureVerificationFalse);

    //查询是否有异步通知处理的结果
    Recharge queryOrderStatus(String orderId);
}
