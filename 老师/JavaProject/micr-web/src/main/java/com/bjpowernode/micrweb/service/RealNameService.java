package com.bjpowernode.micrweb.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.micrweb.config.JdwxRealNameConfig;
import com.bjpowernode.service.UserService;
import com.bjpowernode.util.HttpClientUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class RealNameService {

    @Resource
    private JdwxRealNameConfig realNameConfig;

    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    private UserService userService;

    //处理实名认证
    public boolean handlerRealName(String phone,String idCard,String name){
        boolean result = false;
        //1.调用第三方接口
        boolean isOk = invokeRealNameApi(idCard,name);
        if(isOk){
            //处理数据，更新u_user
            result = userService.realName(phone,name,idCard);
        }
        return result;
    }


    //调用第三方接口
    public boolean invokeRealNameApi(String idCard,String name){

        boolean isOk = false;
        Map<String, String> params = new HashMap<>();
        params.put("cardNo",idCard);
        params.put("realName",name);
        params.put("appkey",realNameConfig.getAppkey());
        try{
           //String result =  HttpClientUtils.doGet(realNameConfig.getUrl(),params);
            String result = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 1305,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": {\n" +
                    "        \"error_code\": 0,\n" +
                    "        \"reason\": \"成功\",\n" +
                    "        \"result\": {\n" +
                    "            \"realname\": \""+name+"\",\n" +
                    "            \"idcard\": \""+idCard+"\",\n" +
                    "            \"isok\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";

            if( result != null && !result.equals("")){
                JSONObject jsonObject = JSONObject.parseObject(result);
                if("10000".equals(jsonObject.getString("code"))){
                    isOk = jsonObject.getJSONObject("result")
                            .getJSONObject("result")
                            .getBooleanValue("isok");

                }

            }
        }catch (Exception e){
            isOk = false;
            e.printStackTrace();
        }
        return isOk;
    }
}
