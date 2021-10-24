package com.nuguseiyou.micrweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.constant.YlbKey;
import com.nuguseiyou.model.Recharge;
import com.nuguseiyou.model.User;
import com.nuguseiyou.util.HttpClientUtils;
import com.nuguseiyou.util.UtilParameterCheck;
import com.nuguseiyou.vo.Pageinfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class RechargeController extends BaseController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${micrPay.kqUrl}")
    private String micrPaykqUrl;

    @Value("${micrPay.kqQueryUrl}")
    private String kqQueryUrl;

    //跳转到充值页
    @GetMapping("/recharge/page/toRecharge")
    public String toRecharge(Model model){

        String orderId = createOrderId();

        model.addAttribute("micrPaykqUrl",micrPaykqUrl);
        model.addAttribute("orderId",orderId);
        return "toRecharge";
    }

    //查询充值结果
    @GetMapping("/recharge/result")
    public String rechargeResult(String orderId,String micrPaykqUrl,Model model){
        if(StringUtils.isAnyEmpty(orderId,micrPaykqUrl)){
            model.addAttribute("trade_msg","充值参数有误");
            return "toRechargeBack";
        }else {
            //向pay服务发送请求 pay服务来实现订单补偿
            //查询数据库查询订单处理结果
            Recharge recharge = rechargeService.queryOrderStatus(orderId);
            if (recharge != null) {
                if(recharge.getRechargeStatus() == YlbConstant.USER_RECHARGE_ON){ //充值状态==0 , 商家没有收到kq的异步通知
                    //没有充值结果,给pay服务发送请求,pay服务调用kq查询订单接口
                    Map<String,String> map = new HashMap<>();
                    map.put("orderId",orderId);
                    try {
                        String result = HttpClientUtils.doGet(kqQueryUrl,map);
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        if(jsonObject.getBooleanValue("success")){
                            //充值处理成功
                            return "forward:/user/page/centre";
                        }else{
                            //充值失败
                            model.addAttribute("trade_msg",jsonObject.getString("msg"));
                            return "toRechargeBack";
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    return "forward:/user/page/centre";
                }
            }else{
                model.addAttribute("micrPaykqUrl",micrPaykqUrl);
                model.addAttribute("orderId",createOrderId());
                return "toRecharge";
            }
        }
        //跳转到充值页
        model.addAttribute("micrPaykqUrl",micrPaykqUrl);
        model.addAttribute("orderId",createOrderId());
        return "toRecharge";
    }

    @GetMapping("/recharge/all")
    public String rechargeAll(@RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "6") Integer pageSize,
                              HttpSession session,
                              Model model) {

        List<Recharge> recharges = new ArrayList<>();
        Pageinfo pageinfo = new Pageinfo();
        //从session中取出user
        User user = (User) session.getAttribute(YlbConstant.YLB_SESSION_USER);
        if (pageNo > 0 && pageSize > 0) {
            pageNo = UtilParameterCheck.parameterPageNoCheck(pageNo);
            pageSize = UtilParameterCheck.parameterPageSizeCheck(pageSize);
            //用户最近充值记录
            recharges = rechargeService.queryUserRechargeRecord(user.getId(),pageNo,pageSize);
            //分页对象
            pageinfo = new Pageinfo(pageNo,pageSize,rechargeService.queryUserRechargeRecordCount(user.getId()));
        }

        model.addAttribute("recharges",recharges);
        model.addAttribute("page",pageinfo);
        return "myRecharge";
    }

    //生成订单号的方法
    public String createOrderId(){
        //生成订单号 时间戳 + redis中自增方法incr 确保订单号唯一
        String orderId = DateFormatUtils.format(new Date() ,"yyyyMMddHHmmssSSS")
                +stringRedisTemplate.opsForValue().increment(YlbKey.RECHARGE_ORDER_SEQ);
        //将订单号放到redis中
        stringRedisTemplate.opsForValue().set(YlbKey.RECHARGE_ORDER,orderId);

        return orderId;
    }


}
