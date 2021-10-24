package com.bjpowernode.micrweb.controller;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.entity.Product;
import com.bjpowernode.service.InvestService;
import com.bjpowernode.service.UserService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    /***************项目首页***************@RequestMapping("") */
    @GetMapping({"/index","/",""})
    public String index(Model model){
        System.out.println("==========index=============");
        //调用远程服务
        Integer registerUsers = userService.queryRegisterUsers();
        model.addAttribute("registerUsers",registerUsers);

        //调用总计投资金额
        BigDecimal sumInvestMoney = investService.querySumInvestMoney();
        model.addAttribute("sumInvestMoney",sumInvestMoney);

        //收益率平均值
        BigDecimal avgProductRate = productService.queryAvgRate();
        model.addAttribute("avgProductRate",avgProductRate);

        //新手宝产品
        List<Product> xinShouBaoList = productService
                .queryProuductPage(YLBContants.PRODUCT_TYPE_XINSHOUBAO,1,1);
        model.addAttribute("xinShouBaoList",xinShouBaoList);

        //优选 4 个
        List<Product> youXuanList = productService
                .queryProuductPage(YLBContants.PRODUCT_TYPE_YOUXUAN,1,4);
        model.addAttribute("youXuanList",youXuanList);

        //散标 8 个
        List<Product> sanBiaoList= productService
                .queryProuductPage(YLBContants.PRODUCT_TYPE_SANBIAO,1,8);
        model.addAttribute("sanBiaoList",sanBiaoList);


        //逻辑视图
        return "index";
    }
}
