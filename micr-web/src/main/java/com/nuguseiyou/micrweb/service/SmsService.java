package com.nuguseiyou.micrweb.service;

import com.alibaba.fastjson.JSONObject;
import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.constant.YlbKey;
import com.nuguseiyou.micrweb.config.JdwxSmsConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 2021/9/17
 */
@Service
public class SmsService {

    @Resource
    private JdwxSmsConfig smsConfig;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param phone 手机号
     * @param cmd   注册时验证码处理
     * @return      发送短信最终处理
     */
    public boolean handleSmsService(String phone, String cmd){
        String smsCode = generateSmsCode(6);
        System.out.println("----------------------------------------------------------");
        System.out.println("smsCode = " + smsCode);
        //调用发送短信的方法
        boolean isSend = sendSms(phone,cmd,smsCode);
        if(isSend){
            //redis存储
            String key = YlbKey.SMS_REG_CODE + phone;
            stringRedisTemplate.opsForValue().set(key,smsCode, YlbConstant.SMS_REG_CODE_TIME, TimeUnit.MINUTES);
        }
        return isSend;
    }

    //发送短信的方法
    private boolean sendSms(String phone, String cmd,String smsCode) {
        boolean isSend = false;
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "";
        if("regist".equals(cmd)){
            //注册时发送短信验证码
            String smsContent = String.format(smsConfig.getContent(), smsCode);
            //https://way.jd.com/chuangxin/dxjk?mobile=13568813957&content=【创信】你的验证码是：5873，3分钟内有效！&appkey=您申请的APPKEY
            url = smsConfig.getUrl() + "?mobile=" + phone + "&content=" + smsContent + "&appkey=" + smsConfig.getAppkey();
        }else if("login".equals(cmd)){
            //登陆时发送短信验证码
        }
        HttpGet get = new HttpGet(url);
        try {
            //CloseableHttpResponse response = client.execute(get);
            //if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            if (true) {
                //String json = EntityUtils.toString(response.getEntity());
                String json = "{\n" +
                        "    \"code\": \"10000\",\n" +
                        "    \"charge\": false,\n" +
                        "    \"remain\": 1305,\n" +
                        "    \"msg\": \"查询成功\",\n" +
                        "    \"result\": {\n" +
                        "        \"ReturnStatus\": \"Success\",\n" +
                        "        \"Message\": \"ok\",\n" +
                        "        \"RemainPoint\": 420842,\n" +
                        "        \"TaskID\": 18424321,\n" +
                        "        \"SuccessCounts\": 1\n" +
                        "    }\n" +
                        "}";
                JSONObject jsonObject = JSONObject.parseObject(json);
                if ("10000".equals(jsonObject.getString("code"))) {
                    JSONObject resultJsonObject = jsonObject.getJSONObject("result");
                    if (resultJsonObject != null) {
                        isSend = "Success".equalsIgnoreCase(resultJsonObject.getString("ReturnStatus"));
                    }
                }
            }
        } catch (Exception e) {
            isSend = false;
            e.printStackTrace();
        }
        return isSend;
    }

    //生成验证码
    private String generateSmsCode(int count) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < count; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    public void removeKey(String key){
        if(stringRedisTemplate.hasKey(key)){
            stringRedisTemplate.delete(key);
        }
    }

    /**
     * @return  校验验证码是否正确
     */
    public boolean verifyAuthCode(String phone, String messageCode){
        boolean flag = false;
        String codeKey = YlbKey.SMS_REG_CODE + phone;

        if(stringRedisTemplate.hasKey(codeKey)){
            String code = stringRedisTemplate.opsForValue().get(codeKey);
            if(messageCode.equals(code)){
                flag = true;
            }
        }
        return flag;
    }
}
