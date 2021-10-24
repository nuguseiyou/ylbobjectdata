package com.bjpowrnode;

import com.bjpowrnode.task.TaskList;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

//启动Dubbo服务
@EnableDubbo
//启动定时任务
@EnableScheduling
@SpringBootApplication
public class TaskListApp {
    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(TaskListApp.class,args);

        TaskList task  = (TaskList) ctx.getBean("taskList");
        //task.invokeGenerateIncomePlan();
        task.invokeGenerateIncomeBack();
    }
}
