package com.nuguseiyou.micrweb.controller;

import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.constant.YlbKey;
import com.nuguseiyou.micrweb.service.RealNameService;
import com.nuguseiyou.micrweb.service.SmsService;
import com.nuguseiyou.model.FinanceAccount;
import com.nuguseiyou.model.Recharge;
import com.nuguseiyou.model.User;
import com.nuguseiyou.model.ext.UserBid;
import com.nuguseiyou.model.ext.UserIncome;
import com.nuguseiyou.util.UtilParameterCheck;
import com.nuguseiyou.vo.CodeEnum;
import com.nuguseiyou.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

/**
 * 2021/9/15
 */
@Controller
public class UserController extends BaseController {

    @Resource
    private SmsService smsService;
    @Resource
    private RealNameService realNameService;

    //跳转到注册页面
    @GetMapping("/user/page/register")
    public String login(String phone) {
        return "register";
    }

    //跳转到实名认证页面
    @GetMapping("/user/page/realName")
    public String realNamePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute(YlbConstant.YLB_SESSION_USER);
        model.addAttribute("phone", user.getPhone());
        return "realName";
    }

    //实名认证后页面跳转
    @PostMapping("/user/realName")
    @ResponseBody
    public Result realName(@RequestParam String phone,
                           @RequestParam String realName,
                           @RequestParam String idCard,
                           HttpSession session) {
        User user = (User) session.getAttribute(YlbConstant.YLB_SESSION_USER);
        Result result = Result.fail();
        if (StringUtils.isAnyEmpty(phone, realName, idCard)) {
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else if (!UtilParameterCheck.checkPhoneFormat(user.getPhone())) {
            result.setCodeEnum(CodeEnum.RC_PHONE_FORMAT_ERROR);
        } else if (realName.length() < 2) {
            result.setCodeEnum(CodeEnum.RC_REALNAME_FORMAT_ERROR);
        } else if (!phone.equals(user.getPhone())) {
            result.setCodeEnum(CodeEnum.RC_PHONE_ERROR);
        } else {
            boolean flag = realNameService.handlerRealName(user.getPhone(), realName, idCard);
            if (flag) {
                //给session中的user添加name 标识这个用户已经进行实名认证
                user.setName(realName);
                //认证成功
                result = Result.ok();
            }
        }
        return result;
    }

    //认证成功后跳转至用户中心页面
    @GetMapping("/user/page/centre")
    public String userCentre(HttpSession session,Model model) {
        User user = (User) session.getAttribute(YlbConstant.YLB_SESSION_USER);
        FinanceAccount account = financeAccountService.queryAccount(user.getId());
        if(account!=null){
            model.addAttribute("accountMoney",account.getAvailableMoney());
        }
        //用户的最近投资记录
        List<UserBid> userBids = investService.queryUserBidRecord(user.getId(),1,5);
        model.addAttribute("userBids",userBids);
        //用户的最近充值记录
        List<Recharge> recharges = rechargeService.queryUserRechargeRecord(user.getId(),1,5);
        model.addAttribute("recharges",recharges);
        //用户的最近收益记录Income
        List<UserIncome> incomes = incomeService.queryUserIncomeRecord(user.getId(),1,5);
        model.addAttribute("incomes",incomes);

        return "myCenter";
    }

    //跳转到登录页面
    @GetMapping("/user/page/login")
    public String login(HttpServletRequest request, String returnUrl, Model model) {
        if (StringUtils.isAnyEmpty(returnUrl)) {
            //http://localhost:8080/ylb/index
            returnUrl = request.getScheme() + "://" + request.getServerName() + ":"
                    + request.getServerPort()
                    + request.getContextPath()
                    + "/index";
            //System.out.println(returnUrl);
        }
        model.addAttribute("returnUrl", returnUrl);
        //注册用户总数
        Integer regiesterUsers = userService.queryRegisterUsers();
        model.addAttribute("regiesterUsers", regiesterUsers);
        //总成交金额
        BigDecimal investMoney = investService.querySumInvestMoney();
        model.addAttribute("investMoney", investMoney);
        //平均收益率
        BigDecimal avgRate = productService.queryavgrate();
        model.addAttribute("avgRate", avgRate);
        return "login";
    }

    //用户登录
    @PostMapping("/user/login")
    @ResponseBody
    public Result userLogin(@RequestParam("phone") String phone,
                            @RequestParam("mima") String mima,
                            HttpSession session) {
        Result result = Result.fail();
        if (StringUtils.isAnyEmpty(phone, mima)) {
            result.setCodeEnum(CodeEnum.RC_PHONE_PWD_NULL);
        } else if (!UtilParameterCheck.checkPhoneFormat(phone)) {
            result.setCodeEnum(CodeEnum.RC_PHONE_FORMAT_ERROR);
        } else if (mima.length() != 32) {
            result.setCodeEnum(CodeEnum.RC_PHONE_PWD_ERROR);
        } else {
            //参数验证通过,查询数据库是否存在该用户,如果存在则修改最后登陆时间
            User user = userService.userLogin(phone, mima);
            if (user != null) {
                //将用户保存到session中
                session.setAttribute(YlbConstant.YLB_SESSION_USER,user);
                result = Result.ok();
            }else {
                result.setCodeEnum(CodeEnum.RC_PHONE_PWD_ERROR);
            }
        }
        return result;
    }

    //主页显示账户余额
    @GetMapping("/user/account")
    @ResponseBody
    public Result userAccount(HttpSession session){
        Result<String> result = new Result<>();
        User user = (User) session.getAttribute(YlbConstant.YLB_SESSION_USER);
        if(user!=null){
            FinanceAccount account = financeAccountService.queryAccount(user.getId());
            if(account!=null){
                result = Result.ok();
                result.setData(account.getAvailableMoney().toPlainString());
            }
        }
        return result;
    }

    //用户退出登录
    @GetMapping("/user/logout")
    public String userLogout(HttpServletRequest request){
        HttpSession session = request.getSession();
        //让会话中的session失效
        session.invalidate();

        //重定向到主页
        return "redirect:/index";
    }


    //异步检查手机号是否已经注册过
    @GetMapping("/user/regist/phone")
    @ResponseBody
    public Result registPhone(@RequestParam("phone") String phone) {
        Result<String> result = Result.fail();
        if (UtilParameterCheck.checkPhoneFormat(phone)) {
            User user = userService.queryPhone(phone);
            if (user == null) {
                result = Result.ok();
            } else {
                result.setCodeEnum(CodeEnum.RC_EDITS_PHONE);
            }
        }
        return result;
    }

    //用户注册
    @PostMapping("/user/register")
    @ResponseBody
    public Result addUser(@RequestParam String phone,
                          @RequestParam String mimaMd5,
                          @RequestParam String messageCode,
                          HttpSession session) {
        Result<String> result = Result.fail();
        //验证参数
        if (StringUtils.isAnyEmpty(phone, mimaMd5, messageCode)) {
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else if (!UtilParameterCheck.checkPhoneFormat(phone)) {
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else if (mimaMd5.length() != 32) {
            result.setCodeEnum(CodeEnum.RC_FORMAT_ERROR);
        } else if (!smsService.verifyAuthCode(phone, messageCode)) {
            result.setCodeEnum(CodeEnum.RC_SMS_CODE_ERROR);
        } else {
            //注册的业务逻辑
            User user = userService.userRegister(phone, mimaMd5);
            if ("register".equals(user.getSources())) {
                result = result.ok();
                //添加成功,删除redis中保存短信验证码的key
                smsService.removeKey(YlbKey.SMS_REG_CODE + phone);
                //把用户存放到HTTPsession 进行实名注册
                session.setAttribute(YlbConstant.YLB_SESSION_USER, user);
            } else {
                result.setCodeEnum(CodeEnum.RC_EDITS_PHONE);
            }

        }

        return result;
    }

}
