package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.entity.FinanceAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface FinanceAccountMapper {

    int insert(FinanceAccount record);

    //查询资金账户
    FinanceAccount selectUid(@Param("uid") Integer userId);

    //查询资金账户上锁
    FinanceAccount selectUidForUpdate(@Param("uid") Integer userId);


    //投资，减少账户资金余额
    int reduceAccountAvailableMoney(@Param("uid") Integer userId, @Param("bidMoney") BigDecimal bidMoney);

    //收益返还，更新资金余额
    int updateMoneyIncomeBack(@Param("uid") Integer uid, @Param("bidMoney") BigDecimal bidMoney, @Param("incomeMoney") BigDecimal incomeMoney);

    //充值，修改金额
    int updateMoneyRecharge(@Param("uid") Integer uid, @Param("rechargeMoney") BigDecimal rechargeMoney);
}