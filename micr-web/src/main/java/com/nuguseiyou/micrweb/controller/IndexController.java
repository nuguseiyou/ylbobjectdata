package com.nuguseiyou.micrweb.controller;

import com.nuguseiyou.model.Product;
import com.nuguseiyou.constant.YlbConstant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * 2021/9/13
 */
@Controller
public class IndexController extends BaseController {

    @GetMapping({"/index","/",""})
    public String index(Model model){
        //调用 注册总人数服务
        Integer regiesterUsers = userService.queryRegisterUsers();
        model.addAttribute("regiesterUsers", regiesterUsers);
        //调用 成交总金额服务
        BigDecimal investMoney = investService.querySumInvestMoney();
        model.addAttribute("investMoney", investMoney);
        //调用 查询年利率服务
        BigDecimal avgRate = productService.queryavgrate();
        model.addAttribute("avgRate", avgRate);
        //调用 展示新手宝产品信息
        List<Product> xinproducts = productService.queryProductInfo(YlbConstant.XINSHOUBAO_TYPE, 1, 1);
        model.addAttribute("xinproducts", xinproducts);
        //展示 优选产品信息
        List<Product> youproducts = productService.queryProductInfo(YlbConstant.YOUXUAN_TYPE, 1, 4);
        model.addAttribute("youproducts", youproducts);
        //展示散标产品信息
        List<Product> sanproducts = productService.queryProductInfo(YlbConstant.SANBIAO_TYPE, 1, 8);
        model.addAttribute("sanproducts", sanproducts);
        return "index";
    }

}

