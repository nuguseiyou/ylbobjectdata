package com.bjpowernode.micrweb.controller;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.contants.YLBKey;
import com.bjpowernode.entity.FinanceAccount;
import com.bjpowernode.entity.Product;
import com.bjpowernode.entity.User;
import com.bjpowernode.entity.ext.PhoneBid;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.InvestTop;
import com.bjpowernode.vo.PageInfo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class ProductController extends BaseController{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /*************产品详情（一个产品的详细信息）*******************/
    @GetMapping("/product/info")
    public String productInfo(@RequestParam("pid") Integer pid, Model model, HttpSession session){

        if( pid != null && pid > 0){
            //获取产品信息， 根据主键获取一个产品的信息。
            Product product = productService.queryProductId(pid);
            if( product != null){

                //查询用户的资金， 从session中获取登陆的user，再获取userId
                User user = (User) session.getAttribute(YLBContants.YLB_SESSION_USER);
                if( user != null){
                    FinanceAccount account = financeAccountService.queryAccountUid(user.getId());
                    if( account != null){
                        //资金余额
                        model.addAttribute("accountMoney",account.getAvailableMoney());
                    }
                }

                //查询投资记录
                List<PhoneBid> phoneBids = investService.queryRecentlyBids(pid);

                model.addAttribute("bidList",phoneBids);
                model.addAttribute("product",product);
                return "productInfo";
            } else {
                model.addAttribute("msg","无此产品");
                return "err";
            }
        }  else {
            model.addAttribute("msg","无此产品");
            return "err";
        }

    }

    /*************分类查询产品*******************/
    @GetMapping("/product/list")
    public String productlist(@RequestParam("ptype") Integer productType,
                              @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                              @RequestParam(value = "pageSize" ,required = false,defaultValue = "9") Integer pageSize,
                              Model model){

        List<Product> productList = new ArrayList<>();
        if(YLBUtil.checkProductType(productType)){
            pageNo = YLBUtil.defaultPageNo(pageNo);
            pageSize = YLBUtil.defaultPageSize(pageSize);
            //获取产品数据
            productList = productService.queryProuductPage(productType,pageNo,pageSize);

            //某类型的总记录数量
            int totalRecords = productService.queryTotalRecordProductType(productType);
            PageInfo pageInfo = new PageInfo(pageNo,pageSize,totalRecords);

            // @TODO 投资排行榜
            Set<ZSetOperations.TypedTuple<String>> sets = stringRedisTemplate.opsForZSet()
                                          .reverseRangeWithScores(YLBKey.INVEST_TOP_LIST, 0, 4);

            List<InvestTop> topList = new ArrayList<>();
            //遍历set集合
            sets.forEach( s->{
                topList.add( new InvestTop(s.getValue(),s.getScore()));
            });



            model.addAttribute("topList",topList);
            model.addAttribute("productType",productType);
            model.addAttribute("pageInfo",pageInfo);
            model.addAttribute("productList",productList);

            return "productList";
        } else {
            model.addAttribute("msg","无此类型的产品");
            return "err";
        }
    }
}
