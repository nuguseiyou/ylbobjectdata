package com.bjpowernode.micrweb.controller;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.contants.YLBKey;
import com.bjpowernode.entity.FinanceAccount;
import com.bjpowernode.entity.Recharge;
import com.bjpowernode.entity.User;
import com.bjpowernode.entity.ext.ProudctNameBid;
import com.bjpowernode.entity.ext.UserIncome;
import com.bjpowernode.micrweb.service.RealNameService;
import com.bjpowernode.micrweb.service.SmsService;
import com.bjpowernode.service.InvestService;
import com.bjpowernode.service.UserService;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.CodeEnum;
import com.bjpowernode.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.ref.ReferenceQueue;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class UserController extends BaseController {

    @Resource
    private SmsService smsService;
    @Resource
    private RealNameService realNameService;

    //登陆页面
    @GetMapping("/user/page/login")
    public String pageUserLogin(String returnUrl, HttpServletRequest request,Model model){
        System.out.println("returnUrl="+returnUrl);

        if( StringUtils.isEmpty(returnUrl)){
            //获取请求的地址信息
            returnUrl = request.getScheme()+"://"+request.getServerName()
                    +":"+request.getServerPort()
                    + request.getContextPath()
                    +"/index";

        }
        model.addAttribute("returnUrl",returnUrl);

        //注册用户数
        Integer registerUsers = userService.queryRegisterUsers();
        model.addAttribute("registerUsers",registerUsers);

        //成交金额
        BigDecimal sumInvestMoney = investService.querySumInvestMoney();
        model.addAttribute("sumInvestMoney",sumInvestMoney);

        //收益率平均值
        BigDecimal avgRate = productService.queryAvgRate();
        model.addAttribute("avgProductRate",avgRate);

        return "login";
    }
    //注册页面
    @GetMapping("/user/page/regist")
    public String pageUserRegist(){
        return "register";
    }


    //用户中心
    @GetMapping("/user/page/mycenter")
    public String pageUserMyCenter(HttpSession session,Model model){
        //资金余额
        User user  = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);
        FinanceAccount account = financeAccountService.queryAccountUid(user.getId());
        if( account != null){
            model.addAttribute("accountMoney",account.getAvailableMoney());
        }

        //投资记录
        List<ProudctNameBid> bidList = investService.queryBidListUid(user.getId(), 1, 5);
        model.addAttribute("bidList",bidList);


        //充值记录
        List<Recharge> rechargeList = rechargeService.queryListUid(user.getId(),1,5);
        model.addAttribute("rechargeList",rechargeList);

        //收益记录
        List<UserIncome> incomeList = incomeService.queryIncomeListUid(user.getId(),1,5);
        model.addAttribute("incomeList",incomeList);

        return "myCenter";
    }

    //实名认证
    @GetMapping("/user/page/realname")
    public String pageRealName(Model model,HttpSession session) {

        User user  = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);
        model.addAttribute("phone",user.getPhone());

        return "realName";
    }

    //登陆
    @PostMapping("/user/login")
    @ResponseBody
    public Result userLogin(@RequestParam("phone") String phone,
                            @RequestParam("mima") String loginPassword,
                            HttpSession session){
        Result<String> result = new Result<>();
        //验证数据
        if( StringUtils.isAnyEmpty(phone,loginPassword)){
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else if( !YLBUtil.checkFormatPhone(phone)){
            result.setCodeEnum(CodeEnum.RC_FORMAT_PHONE_ERROR);
        } else if( loginPassword.length() !=32 ){
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else {
            //参数是正确的 ，调用数据服务处理数据库的操作
            User user = userService.userLogin(phone,loginPassword);
            //登陆成功，把登陆的用户存放session
            if(user != null){
                session.setAttribute(YLBContants.YLB_SESSION_USER,user);
                result = Result.ok();
            } else {
                result.setCodeEnum(CodeEnum.RC_PHONE_PASSWORD_INVLIDATE);
            }
        }

        return result;
    }

    //退出系统
    @GetMapping("/user/logout")
    public String userLogout(HttpServletRequest request){
        //session
        HttpSession session = request.getSession();
        //让会话无效
        session.invalidate();

        //重定向到首页
        return "redirect:/index";
    }

    //查询资金
    @GetMapping("/user/account")
    @ResponseBody
    public Result queryAccount(HttpSession session){
        Result<String> result = new Result<>();

        User user  = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);
        FinanceAccount account = financeAccountService.queryAccountUid(user.getId());
        if( account != null){
            result = Result.ok();
            result.setData(account.getAvailableMoney().toPlainString());
        }

        return result;
    }



    //实名认证的逻辑
    @PostMapping("/user/realname")
    @ResponseBody
    public Result userRealName(@RequestParam("phone") String phone,
                               @RequestParam("idcard") String idCard,
                               @RequestParam("name") String name,
                               HttpSession session){

        Result<String> result = Result.fail();

        User user  = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);
        //验证数据
        if( StringUtils.isAnyEmpty(phone,idCard,name)){
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else if( !YLBUtil.checkFormatPhone(phone)){
            result.setCodeEnum(CodeEnum.RC_FORMAT_PHONE_ERROR);
        } else if( name.length() < 2){
            result.setCodeEnum(CodeEnum.RC_NAME_ERROR);
        } else if( !phone.equals(user.getPhone())){
            result.setCodeEnum(CodeEnum.RC_PHONE_INVLIDATE);
        } else {
            //数据是正确的
            boolean authResult = realNameService.handlerRealName(user.getPhone(),idCard,name);
            if( authResult ){
                //给session中用户增加name信息
                user.setName(name);
                //认证成功
                result = Result.ok();
            }
        }
        return result;
    }


    //手机号是否注册
    @GetMapping("/user/regist/phone")
    @ResponseBody
    public Result hasRegistPhone(@RequestParam("phone") String phone){
        Result<String> result = Result.fail();
        if(YLBUtil.checkFormatPhone(phone) ){
            User user  = userService.queryPhone(phone);
            if( user == null){
                result = Result.ok();
            } else {
                result.setCodeEnum(CodeEnum.RC_EXITS_PHONE);
            }
        }
        return result;
    }


    //用户注册
    @PostMapping("/user/register")
    @ResponseBody
    public Result userRegister(@RequestParam("phone") String phone,
                               @RequestParam("mima") String mima,
                               @RequestParam("code") String code,
                               HttpSession session){
        Result<String> ro  = Result.fail();
        // 检查数据
        if(StringUtils.isAnyEmpty(phone,mima,code)){
            ro.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else if( !YLBUtil.checkFormatPhone(phone)){
            ro.setCodeEnum(CodeEnum.RC_FORMAT_PHONE_ERROR);
        } else if( mima.length() != 32){
            ro.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else if( !smsService.checkAuthCode(phone,code)){
            ro.setCodeEnum(CodeEnum.RC_AUTHCODE_ERROR);
        } else {
            //注册业务逻辑
            User user  = userService.userRegister(phone,mima);
            if( "regist".equals(user.getSource())){
                ro = Result.ok();
                //删除短信验证码的key
                smsService.delRedisKey(YLBKey.SMS_REG_CODE+phone);
                //把user存放到session，下一个功能是实名认证
                session.setAttribute(YLBContants.YLB_SESSION_USER,user);
            } else {
                ro.setCodeEnum(CodeEnum.RC_EXITS_PHONE);
            }
        }
        return ro;
    }

}
