package com.bjpowernode;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//mybatis扫描器
@MapperScan("com.bjpowernode.dataservice.mapper")
//启动dubbo服务
@EnableDubbo
@SpringBootApplication
public class DataServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataServiceApplication.class,args);
    }
}
