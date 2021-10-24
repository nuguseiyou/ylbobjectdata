package com.nuguseiyou.task;

import com.nuguseiyou.service.IncomeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component("task")
public class TaskList {

    @DubboReference(interfaceClass = IncomeService.class ,version = "1.0")
    private IncomeService incomeService;


    //调用生成收益计划的方法
    //设置定时任务 执行时间为 每天凌晨两点
    @Scheduled(cron = "* * 2 * * ?")
    public void invokeCreateIncomeRecord(){
        System.out.println("生成收益计划方法调用 begin");
        incomeService.queryFullProduct();
        System.out.println("生成收益计划方法调用 end");
    }

    //调用到期收益返还方法
    //设置定时任务 执行时间为 每天凌晨一点
    @Scheduled(cron = "* * 1 * * ?")
    public void invokeIncomeReturn(){
        System.out.println("收益返还方法调用 begin");
        incomeService.incomeReturn();
        System.out.println("收益返还计划方法调用 end");
    }

    /*@Scheduled(cron = "0/5 * * * * ?")
    public void task(){
        System.out.println("定时任务测试:     "+ new Date());
    }*/


}
