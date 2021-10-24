package com.nuguseiyou.service;

import com.nuguseiyou.model.FinanceAccount;

public interface FinanceAccountService {
    /**
     * @param uid 用户主键id
     * @return    返回一个资金表对象
     */
    FinanceAccount queryAccount(Integer uid);
}
