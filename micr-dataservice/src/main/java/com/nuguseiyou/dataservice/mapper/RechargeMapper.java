package com.nuguseiyou.dataservice.mapper;

import com.nuguseiyou.model.Recharge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeMapper {

    /**
     *                  查询用户最近充值记录
     * @param uid       用户id
     * @param offset    页码
     * @param rows      页面数据大小
     * @return          返回用户最近充值记录集合
     */
    List<Recharge> selectUserRechargeRecord(@Param("uid") Integer uid, @Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * @param uid 用户id
     * @return    返回用户最近充值记录数
     */
    int selectUserRechargeRecordCount(@Param("uid") Integer uid);

    //创建用户充值记录
    int insertRecharge(Recharge recharge);

    //查询充值订单是否存在
    Recharge selectRecharge(@Param("orderId") String orderId);

    //修改充值记录的充值状态
    int updateRechargeStatus(@Param("orderId") String orderId, @Param("userRechargeTrue") int rechargeStatus);

    //查询是否有异步通知处理的结果
    Recharge selectRechargeStatus(@Param("orderId") String orderId);
}