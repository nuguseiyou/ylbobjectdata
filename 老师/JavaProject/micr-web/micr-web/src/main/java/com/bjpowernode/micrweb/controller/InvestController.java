package com.bjpowernode.micrweb.controller;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.contants.YLBKey;
import com.bjpowernode.entity.User;
import com.bjpowernode.entity.ext.ProudctNameBid;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.CodeEnum;
import com.bjpowernode.vo.PageInfo;
import com.bjpowernode.vo.Result;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.utils.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class InvestController extends BaseController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //产品投资
    @PostMapping("/user/invest")
    @ResponseBody
    public Result userInvest(@RequestParam("productId") Integer productId,
                             @RequestParam("bidMoney") BigDecimal bidMoney,
                             HttpSession session){
        Result<String> result = Result.fail();

        //接收参数的基本的验证
        if(productId != null && productId > 0 && bidMoney !=null
                && bidMoney.intValue() % 100 ==0
                && bidMoney.intValue() >= 100 ){
            //基本数据是正确的，可以投资
            User user = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);
            if( StringUtils.isEmpty(user.getName())){
                //必须先实名认证
                result.setCodeEnum(CodeEnum.RC_NO_REALNAME);
            } else {
                result =  investService.userInvest(user.getId(),productId,bidMoney);
                if( result.isSuccess()){
                    //更新redis，投资排行榜
                    stringRedisTemplate.opsForZSet().incrementScore(
                            YLBKey.INVEST_TOP_LIST,user.getPhone(),bidMoney.doubleValue());

                }
            }
        } else {
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        }
        return result;

    }

    //查询所有投资记录
    @GetMapping("/invest/all")
    public String moreInvest(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                             @RequestParam(value = "pageSize",required = false,defaultValue = "6") Integer pageSize ,
                             HttpSession session,
                             Model model){


        PageInfo pageInfo = new PageInfo();
        //获取投资记录
        List<ProudctNameBid> bidList = new ArrayList<>();
        User user = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);
        if( pageNo != null && pageSize != null){
            pageNo = YLBUtil.defaultPageNo(pageNo);
            pageSize = YLBUtil.defaultPageSize(pageSize);
            bidList= investService.queryBidListUid(user.getId(),pageNo,pageSize);

            //获取总记录数量
            pageInfo  = new PageInfo(pageNo,pageSize,investService.queryBidCountUid(user.getId()));



        }
        model.addAttribute("page",pageInfo);
        model.addAttribute("bidList",bidList);

        return "myInvest";

    }
}
