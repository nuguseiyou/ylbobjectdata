package com.nuguseiyou.micrweb.controller;

import com.nuguseiyou.service.*;
import org.apache.dubbo.config.annotation.DubboReference;

/**
 * 2021/9/13
 */
public class BaseController {
    //引用用户服务
    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    protected UserService userService;

    //引用投资服务
    @DubboReference(interfaceClass = InvestService.class,version = "1.0")
    protected InvestService investService;

    //引用查询年利率服务
    @DubboReference(interfaceClass = ProductService.class,version = "1.0")
    protected ProductService productService;

    //引用资金服务
    @DubboReference(interfaceClass = FinanceAccountService.class,version = "1.0")
    protected FinanceAccountService financeAccountService;

    //引用充值服务
    @DubboReference(interfaceClass = RechargeService.class,version = "1.0")
    protected RechargeService rechargeService;

    //引用收益服务
    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    protected IncomeService incomeService;




}
