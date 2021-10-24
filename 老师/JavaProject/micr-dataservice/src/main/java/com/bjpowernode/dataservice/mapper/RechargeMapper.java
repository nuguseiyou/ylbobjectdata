package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.entity.Recharge;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RechargeMapper {

    //用户的充值记录
    List<Recharge> selectRechargesUid(@Param("uid") Integer uid,
                                      @Param("offSet") int offSet,
                                      @Param("rows") Integer rows);

    //用户充值记录总数
    int selectCountUid(@Param("uid") Integer uid);

    //创建充值记录
    int insert(Recharge recharge);

    //根据充值订单号，查询订单
    Recharge selectRechargeNoLock(@Param("orderId") String orderId);

    //更新充值记录的状态
    int updateRechargeStatus(@Param("id") Integer id, @Param("newStatus") Integer newStatus);

    //通过充值订单号，更新状态
    int updateRechargeStatusByRechargeNo(@Param("rechargeNo") String orderId, @Param("newStatus") Integer newStatus);

    //充值记录
    Recharge selectByRechargeNo(@Param("rechargeNo") String rechargeNo);
}