package com.bjpowernode.micrweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.contants.YLBKey;
import com.bjpowernode.entity.Recharge;
import com.bjpowernode.entity.User;
import com.bjpowernode.util.HttpClientUtils;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.dubbo.common.json.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RechargeController extends BaseController {

    @Value("${micrPay.kqUrl}")
    private String micrPayKqUrl;

    @Value("${micrPay.kqOrderUrl}")
    private String micrPayKqOrderQueryUrl;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //进入充值页面
    @GetMapping("/recharge/page/toRecharge")
    public String pageRechargeIndex(Model model){
        //生成充值订单号， 一般使用时间戳比较多。 时间戳+唯一自增值
        //20210928094448123 + 随机数6位
        //20210928094448123  + redis的自增值（incr命令）
        String recharge_no = generateNewOrderId();
        model.addAttribute("rechargeNo",recharge_no); //recharge_no
        model.addAttribute("kqUrl",micrPayKqUrl);
        return "toRecharge";
    }

    //查询充值结果
    @GetMapping("/recharge/result")
    public String rechargeResult(String rechargeNo, String kqUrl,Model model){
        if(StringUtils.isAnyEmpty(rechargeNo,kqUrl)){
            model.addAttribute("trade_msg","充值数据有误");
            return "toRechargeBack";
        } else {
            //1.先查询数据库，充值记录，看订单是否有异步通知结果
            Recharge recharge = rechargeService.queryForRechargeNo(rechargeNo);
            if( recharge != null ){
                if( recharge.getRechargeStatus() == YLBContants.RECHARGE_STATUS_PROCESSING){
                    //2.没有充值结果， 调用快钱的查询接口
                    Map<String,String> map  = new HashMap<>();
                    map.put("rechargeNo",rechargeNo);
                    try{
                        String result = HttpClientUtils.doGet(micrPayKqOrderQueryUrl, map);
                        //json string
                        JSONObject jsonObject = JSONObject.parseObject(result);

                        if( jsonObject.getBooleanValue("success")){
                            //充值处理完毕
                            return "forward:/user/page/mycenter";
                        } else {
                            //处理失败
                            model.addAttribute("trade_msg", jsonObject.getString("msg"));
                            return "toRechargeBack";
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    //充值处理完毕
                    return "forward:/user/page/mycenter";
                }
            }
        }
        //重新充值
        model.addAttribute("kqUrl",kqUrl);
        model.addAttribute("rechargeNo",generateNewOrderId());
        //暂时到充值页面，后期会改
        return "toRecharge";
    }

    @GetMapping("/recharge/all")
    public String moreRecharge(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "6") Integer pageSize,
                               HttpSession session,
                               Model model){
        User user  = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);

        pageNo = YLBUtil.defaultPageNo(pageNo);
        pageSize = YLBUtil.defaultPageSize(pageSize);
        List<Recharge> rechargeList  = rechargeService.queryListUid(user.getId(),pageNo,pageSize);

        //记录数量
        int rows  = rechargeService.queryCountUid(user.getId());
        PageInfo pageInfo  = new PageInfo(pageNo,pageSize,rows);

        model.addAttribute("rechargeList",rechargeList);
        model.addAttribute("page",pageInfo);

        return "myRecharge";
    }


    private String generateNewOrderId(){
        String recharge_no = DateFormatUtils.format(new Date(),"yyyyMMddHHmmssSSS") +
                stringRedisTemplate.opsForValue().increment(YLBKey.RECHARGE_ORDERID_SEQ);
        //存放到redis
        stringRedisTemplate.opsForValue().set(YLBKey.RECHARGE_ORDERID+recharge_no,recharge_no);

        return recharge_no;
    }
}
