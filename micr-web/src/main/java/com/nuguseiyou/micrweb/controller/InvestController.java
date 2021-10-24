package com.nuguseiyou.micrweb.controller;

import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.constant.YlbKey;
import com.nuguseiyou.model.User;
import com.nuguseiyou.model.ext.UserBid;
import com.nuguseiyou.util.UtilParameterCheck;
import com.nuguseiyou.vo.CodeEnum;
import com.nuguseiyou.vo.Pageinfo;
import com.nuguseiyou.vo.Result;
import org.apache.commons.lang3.StringUtils;
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
public class InvestController extends BaseController{

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @PostMapping("/user/invest")
    @ResponseBody
    public Result userInvest(@RequestParam("productId") Integer productId,
                             @RequestParam("bidMoney")BigDecimal bidMoney,
                             HttpSession session){
        Result<String> result = Result.fail();
        //取出session中的user
        User user = (User)session.getAttribute(YlbConstant.YLB_SESSION_USER);
        //验证参数的正确性
        if(productId != null && productId > 0 && bidMoney.intValue() >= 100 && bidMoney.intValue() % 100 == 0){
            //数据正确 可以进行投资
            //判断是否已经进行实名注册
            if(StringUtils.isEmpty(user.getName())){
                //实名认证失败
                result.setCodeEnum(CodeEnum.RC_NO_REALNAME);
            }else{
                //投资
                result = investService.userInvest(user.getId(),productId,bidMoney);
                if(result.isSuccess()){
                    //更新redis中的投资排行榜
                    stringRedisTemplate.opsForZSet().incrementScore(YlbKey.INVEST_TOP_LIST,user.getPhone(),bidMoney.doubleValue());
                }
            }
        }else{
            //参数格式错误
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        }
        return result;
    }


    @GetMapping("/invest/all")
    public String investAll(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                            @RequestParam(value = "pageSize",required = false,defaultValue = "6") Integer pageSize,
                            HttpSession session,
                            Model model){
        List<UserBid> userBids = new ArrayList<>();
        //获取session中的user的id
        User user = (User) session.getAttribute(YlbConstant.YLB_SESSION_USER);
        if(pageNo > 0 && pageSize > 0){
            pageNo = UtilParameterCheck.parameterPageNoCheck(pageNo);
            pageSize = UtilParameterCheck.parameterPageSizeCheck(pageSize);
            userBids = investService.queryUserBidRecord(user.getId(), pageNo, pageSize);
        }
        //分页对象
        Pageinfo pageinfo = new Pageinfo(pageNo,pageSize,investService.queryUserBidRecordCount(user.getId()));

        model.addAttribute("userBids",userBids);
        model.addAttribute("page",pageinfo);
        return "myInvest";
    }
}
