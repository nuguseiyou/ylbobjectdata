package com.bjpowernode.micrweb.controller;

import com.bjpowernode.micrweb.service.SmsService;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.CodeEnum;
import com.bjpowernode.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class SmsController {

    @Resource
    private SmsService smsService;

    //发送短信 phone:给哪个手机号发送短信， cmd表示操作是什么
    @PostMapping("/sms/authcode")
    public Result<String> sendSms(String phone,String cmd){
        Result<String> ro = Result.fail();
        if(YLBUtil.checkFormatPhone(phone)){
            //发送短信
            boolean result = smsService.handlerSmsService(phone,cmd);
            if( result ){
                ro = Result.ok();
            }
        } else {
            ro.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        }
        return ro;
    }


}
