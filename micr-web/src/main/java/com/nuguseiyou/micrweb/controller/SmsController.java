package com.nuguseiyou.micrweb.controller;

import com.nuguseiyou.micrweb.service.SmsService;
import com.nuguseiyou.util.UtilParameterCheck;
import com.nuguseiyou.vo.CodeEnum;
import com.nuguseiyou.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 2021/9/17
 */
@RestController
public class SmsController {

    @Resource
    private SmsService smsService;

    /**
     * @param phone 给那个手机号发送验证码
     * @param cmd   发送验证码的场景
     * @return      发送验证码
     */
    @PostMapping("/sms/authcode")
    public Result<String> sendSms(String phone, String cmd){
        Result<String> ro = Result.fail();
        if(UtilParameterCheck.checkPhoneFormat(phone)){
            boolean b = smsService.handleSmsService(phone, cmd);
            if(b){
                ro = Result.ok();
            }
        }else{
            ro.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        }
        return ro;
    }
}
