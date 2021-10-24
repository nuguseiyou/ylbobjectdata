package com.bjpowernode.micrweb.config;

import com.bjpowernode.micrweb.inteptor.LoginInterceptor;
import org.apache.zookeeper.Login;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class YLBWebSettings implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        String addPath[]= {"/income/all",
                            "/invest/all",
                            "/user/invest",
                            "/recharge/*",
                            "/user/account",
                            "/user/page/mycenter",
                            "/user/page/realname",
                            "/user/realname"
                            };

        registry.addInterceptor( new LoginInterceptor())
                .addPathPatterns(addPath);

    }
}
