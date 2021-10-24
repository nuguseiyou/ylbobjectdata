package com.bjpowernode.dataservice.service;

import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.entity.FinanceAccount;
import com.bjpowernode.service.FinanceAccountService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


@DubboService(interfaceClass = FinanceAccountService.class,version = "1.0")
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Resource
    private FinanceAccountMapper accountMapper;
    /**
     * @param uid 用户id
     * @return 资金账户account 或  null
     */
    @Override
    public FinanceAccount queryAccountUid(Integer uid) {
        FinanceAccount account = null;
        if( uid != null && uid > 0 ){
            account = accountMapper.selectUid(uid);
        }
        return account;
    }
}
