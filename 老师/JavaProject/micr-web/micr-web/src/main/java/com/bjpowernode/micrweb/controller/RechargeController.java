package com.bjpowernode.micrweb.controller;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.entity.Recharge;
import com.bjpowernode.entity.User;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class RechargeController extends BaseController {

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
}
