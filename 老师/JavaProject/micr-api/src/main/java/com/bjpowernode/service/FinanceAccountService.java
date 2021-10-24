package com.bjpowernode.service;

import com.bjpowernode.entity.FinanceAccount;

//资金服务
public interface FinanceAccountService {

    /**
     * @param uid 用户id
     * @return 资金账户
     */
    FinanceAccount queryAccountUid(Integer uid);
}
