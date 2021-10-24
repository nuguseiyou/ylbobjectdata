package com.nuguseiyou.dataservice.mapper;

import com.nuguseiyou.model.FinanceAccount;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

public interface FinanceAccountMapper {

    /**
     * @param record
     * @return     向资金表中添加一条记录
     */
    int insert(FinanceAccount record);

    /**
     * @param uid 用户主键id
     * @return    返回一个资金表对象
     */
    FinanceAccount selectAccount(@Param("uid") Integer uid);

    //查询资金账户并上锁
    FinanceAccount selectMoneyForUpdate(@Param("uid") Integer uid);

    //用户资金表余额扣除投资金额
    int updateAvailable(@Param("userId") Integer userId, @Param("bidMoney") BigDecimal bidMoney);

    //将收益返还到用户资金余额
    int updateAvailableForIncomeReturn(@Param("uid") Integer uid, @Param("incomeMoney") BigDecimal incomeMoney,@Param("bidMoney") BigDecimal bidMoney);

    //将用户充值的金额 更新到用户余额中
    int updateUserAvaliableMoney(@Param("uid") Integer uid, @Param("rechargeMoney") BigDecimal rechargeMoney);
}