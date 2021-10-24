package com.bjpowernode.micrweb.controller;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.entity.Income;
import com.bjpowernode.entity.User;
import com.bjpowernode.entity.ext.UserIncome;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.PageInfo;
import org.apache.dubbo.common.utils.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IncomeController extends BaseController {

    //更多收益记录
    @GetMapping("/income/all")
    public String myIncome(
            @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize",required = false,defaultValue = "6") Integer pageSize,
            HttpSession session, Model model){
        User user = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);

        pageNo = YLBUtil.defaultPageNo(pageNo);
        pageSize = YLBUtil.defaultPageSize(pageSize);

        List<UserIncome> incomeList = incomeService.queryIncomeListUid(
                                    user.getId(),pageNo,pageSize);

        int counts = incomeService.queryIncomeCountUid(user.getId());
        PageInfo page = new PageInfo(pageNo,pageSize,counts);

        model.addAttribute("incomeList",incomeList);
        model.addAttribute("page",page);

        return "myIncome";

    }
}
