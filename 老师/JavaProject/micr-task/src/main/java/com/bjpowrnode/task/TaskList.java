package com.bjpowrnode.task;

import com.bjpowernode.service.IncomeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component("taskList")
public class TaskList {


    @DubboReference(interfaceClass = IncomeService.class,version = "1.0")
    private IncomeService incomeService;
    /**
     * 定义方法，表示定时任务的功能
     * 方法要求：
     * 1.public方法
     * 2.没有返回值
     * 3.没有参数
     */
    //@Scheduled(cron = "*/10 * * * * ?")
    public void testCron(){
        System.out.println("测试定时任务的方法执行："+new Date());
    }

    /**
     * 调用dubbo的服务，生成收益计划
     */
    //@Scheduled(cron = "0 0 2 * * ?")
    public void invokeGenerateIncomePlan(){
        System.out.println("开始执行生成收益计划的方法调用--开始");
        incomeService.generateIncomePlan();
        System.out.println("开始执行生成收益计划的方法调用--完成");
    }

    /**
     * 调用dubbo的服务，生成收益计划
     */
    //@Scheduled(cron = "0 0 1 * * ?")
    public void invokeGenerateIncomeBack(){
        System.out.println("开始执行收益返还的方法调用--开始");
        incomeService.generateIncomeBack();
        System.out.println("开始执行收益返还的方法调用--完成");
    }

}
