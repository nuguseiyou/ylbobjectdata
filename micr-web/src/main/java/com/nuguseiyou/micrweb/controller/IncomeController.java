package com.nuguseiyou.micrweb.controller;

import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.model.User;
import com.nuguseiyou.model.ext.UserIncome;
import com.nuguseiyou.util.UtilParameterCheck;
import com.nuguseiyou.vo.Pageinfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IncomeController extends BaseController{

    @GetMapping("/income/all")
    public String incomeAll(@RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                            @RequestParam(value = "paegSize", required = false, defaultValue = "6") Integer pageSize,
                            HttpSession session,
                            Model model) {
        //从session中取出user
        User user = (User) session.getAttribute(YlbConstant.YLB_SESSION_USER);
        List<UserIncome> userIncomes = new ArrayList<>();
        Pageinfo pageinfo = new Pageinfo();
        if (pageNo > 0 && pageSize > 0) {
            pageNo = UtilParameterCheck.parameterPageNoCheck(pageNo);
            pageSize = UtilParameterCheck.parameterPageSizeCheck(pageSize);
            userIncomes = incomeService.queryUserIncomeRecord(user.getId(), pageNo, pageSize);
            //分页对象
            pageinfo = new Pageinfo(pageNo,pageSize,incomeService.queryUserIncomeRecordCount(user.getId()));
        }

        model.addAttribute("userIncomes",userIncomes);
        model.addAttribute("page",pageinfo);
        return "myIncome";
    }
}
