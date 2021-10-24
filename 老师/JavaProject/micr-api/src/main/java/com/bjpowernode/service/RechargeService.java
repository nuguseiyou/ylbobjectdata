package com.bjpowernode.service;

import com.bjpowernode.entity.Recharge;
import com.bjpowernode.vo.Result;

import java.util.List;

//充值服务
public interface RechargeService {

    //用户的充值记录
    List<Recharge> queryListUid(Integer uid, Integer pageNo,Integer pageSize);

    //记录总数
    int queryCountUid(Integer uid);

    //创建充值记录
    int addRecharge(Recharge recharge);

    /**
     * @param orderId    商家订单号
     * @param payResult  充值结果，10：成功， 11失败
     * @param payAmount  充值金额 分为单位
     * @return
     */
    //充值的异步通知
    Result handlerRechargeNotify(String orderId, String payResult, String payAmount);

    //更新状态
    boolean modifyRechargeStatus(String orderId, Integer newStatus);

    //查询充值记录对象
    Recharge queryForRechargeNo(String rechargeNo);
}
