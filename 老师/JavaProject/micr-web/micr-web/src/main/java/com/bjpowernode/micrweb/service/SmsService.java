package com.bjpowernode.micrweb.service;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.contants.YLBKey;
import com.bjpowernode.micrweb.config.JdwsSmsConfig;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class SmsService {

    @Resource
    private JdwsSmsConfig smsConfig;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param phone 手机号
     * @param cmd   命令
     * @return true:发送成功，false 失败
     */
    //处理短信验证的所有业务
    public boolean handlerSmsService(String phone,String cmd){
        //1.调用下发短信
        String authCode = generateSmsCode(6);
        boolean isSend  = sendSms(phone,cmd,authCode);
        //2.redis存储
        if( isSend ){
            String key = YLBKey.SMS_REG_CODE + phone;
            stringRedisTemplate.opsForValue().set(key,authCode,
                    YLBContants.SMS_REG_CODE_TIME, TimeUnit.MINUTES);
        }
        return isSend;
    }
    /**
     * @param phone 手机号
     * @param cmd   命名（regist 注册， login：登陆）
     */
    //发送短信的方法
    private boolean sendSms(String phone,String cmd,String authCode){

        boolean isSend = false;//发送失败
        //使用httpclient库
        CloseableHttpClient client = HttpClients.createDefault();
        String url = "";
        if("regist".equals(cmd)){
            //https://way.jd.com/chuangxin/dxjk?mobile=13568813957&content=【创信】你的验证码是：5873，3分钟内有效！&appkey=您申请的APPKEY
            System.out.println("短信验证码="+authCode);
            String smsContent = String.format( smsConfig.getContent(), authCode);
            url= smsConfig.getUrl()+"?"+"mobile="+phone+"&content="+smsContent+"&appkey="+smsConfig.getAppkey();

        } else if( "login".equals(cmd)){
            //登陆的短信发送
        }
        HttpGet get = new HttpGet(url);
        try{
            //CloseableHttpResponse resp = client.execute(get);
            //if( resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
            if( true ){
                //String json = EntityUtils.toString(resp.getEntity());

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

                //使用fastjson解析数据
                JSONObject jsonObject = JSONObject.parseObject(json);
                if("10000".equals(jsonObject.getString("code"))){

                    JSONObject resultObject = jsonObject.getJSONObject("result");
                    if( resultObject != null){
                        isSend = "Success".equalsIgnoreCase(resultObject.getString("ReturnStatus"));
                    }
                }
            }
        } catch (Exception e){
            isSend = false;
            e.printStackTrace();
        }
        return isSend;
    }


    //检查验证码是否正确
    public boolean checkAuthCode(String phone,String code){
        boolean auth = false;
        String key = YLBKey.SMS_REG_CODE + phone;
        if( stringRedisTemplate.hasKey(key)){
            String redisDbAuthCode = stringRedisTemplate.opsForValue().get(key);
            if( code.equals(redisDbAuthCode) ){
                auth = true;
            }
        }
        return auth;
    }

    //删除key
    public void delRedisKey(String key){
        stringRedisTemplate.delete(key);
    }

    //生成验证码
    private String generateSmsCode(int len){

        StringBuilder builder = new StringBuilder("");
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for(int i=0;i<len;i++){
            builder.append(random.nextInt(10));
        }
        return builder.toString();

    }

}
