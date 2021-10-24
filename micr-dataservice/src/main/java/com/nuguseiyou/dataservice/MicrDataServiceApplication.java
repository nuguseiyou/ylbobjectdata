package com.nuguseiyou.dataservice;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 2021/9/13
 */
@MapperScan(value = "com.nuguseiyou.dataservice.mapper")
@EnableDubbo
@SpringBootApplication
public class MicrDataServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicrDataServiceApplication.class,args);
    }
}
