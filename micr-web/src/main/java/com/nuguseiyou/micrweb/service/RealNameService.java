package com.nuguseiyou.micrweb.service;

import com.alibaba.fastjson.JSONObject;
import com.nuguseiyou.micrweb.config.JdwxRealNameConfig;
import com.nuguseiyou.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class RealNameService {

    @Resource
    private JdwxRealNameConfig jdwxRealNameConfig;

    @DubboReference(interfaceClass = UserService.class,version = "1.0")
    private UserService userService;

    //将实名认证数据存储到数据库
    public boolean handlerRealName(String phone,String realName,String idCard){
        boolean flag = false;
        boolean result = invokeRealNameApi(realName, idCard);
        if(result){
            flag = userService.editUserInfo(phone, realName, idCard);
        }
        return flag;
    }

    //调用第三方接口进行二元素实名认证
    private boolean invokeRealNameApi(String realName,String idCard){
        boolean isOk = false;
        CloseableHttpClient client = HttpClients.createDefault();

        Map<String, String> params = new HashMap<>();
        params.put("cardNo",idCard);
        params.put("realName",realName);
        params.put("appkey",jdwxRealNameConfig.getAppkey());
        try {
            //String result = HttpClientUtils.doGet(jdwxRealNameConfig.getUrl(), params);

            //使用模拟数据
            String result = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 1305,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": {\n" +
                    "        \"error_code\": 0,\n" +
                    "        \"reason\": \"成功\",\n" +
                    "        \"result\": {\n" +
                    "            \"realname\": \""+realName+"\",\n" +
                    "            \"idcard\": \""+idCard+"\",\n" +
                    "            \"isok\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            if(result != null && !result.equals("")){
                //使用fastjson解析
                JSONObject jsonObject = JSONObject.parseObject(result);
                if("10000".equals(jsonObject.getString("code"))){
                    isOk = jsonObject.getJSONObject("result").getJSONObject("result").getBooleanValue("isok");
                }
            }
        } catch (Exception e) {
            isOk = false;
            e.printStackTrace();
        }
        return isOk;
    }
}
                /**
                 * {
                 *     "code": "10000",
                 *     "charge": false,
                 *     "remain": 1305,
                 *     "msg": "查询成功",
                 *     "result": {
                 *         "error_code": 0,
                 *         "reason": "成功",
                 *         "result": {
                 *             "realname": "乐天磊",
                 *             "idcard": "350721197702134399",
                 *             "isok": true
                 *         }
                 *     }
                 * }
                 */

