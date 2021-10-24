package com.nuguseiyou.dataservice.service.impl;

import com.nuguseiyou.dataservice.mapper.FinanceAccountMapper;
import com.nuguseiyou.model.FinanceAccount;
import com.nuguseiyou.service.FinanceAccountService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService(interfaceClass = FinanceAccountService.class,version = "1.0")
public class FinanceAccountServiceImpl implements FinanceAccountService{
    @Resource
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public FinanceAccount queryAccount(Integer uid) {
        FinanceAccount account = null;
        if(uid!=null){
            account = financeAccountMapper.selectAccount(uid);
        }
        return account;
    }
}
