package com.nuguseiyou.micrweb.controller;

import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.constant.YlbKey;
import com.nuguseiyou.model.FinanceAccount;
import com.nuguseiyou.model.Product;
import com.nuguseiyou.model.User;
import com.nuguseiyou.model.ext.InvestTopList;
import com.nuguseiyou.model.ext.PhoneBid;
import com.nuguseiyou.util.UtilParameterCheck;
import com.nuguseiyou.vo.Pageinfo;
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

/**
 * 2021/9/14
 */
@Controller
public class ProductController extends BaseController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //产品详情页
    @GetMapping("/product/info")
    public String productInfo(@RequestParam("pid") Integer pid, Model model, HttpSession session){
        //参数检查
        if(pid!=null&& pid>0){
            Product product = productService.queryProductMinuteInfo(pid);
            if (product!=null){
                //从session中取的user对象
                User user = (User) session.getAttribute(YlbConstant.YLB_SESSION_USER);
                if(user!=null){
                    FinanceAccount account = financeAccountService.queryAccount(user.getId());
                    if(account!=null){
                        model.addAttribute("accountMoney",account.getAvailableMoney());
                    }
                }

                //该产品最近投资记录
                List<PhoneBid> phoneBids = investService.queryProductBidRecord(pid);
                model.addAttribute("phoneBids", phoneBids);
                model.addAttribute("product", product);
                return "productinfo";
            }else {
                model.addAttribute("msg", "没有此类产品");
                return "err";
            }
        }else{
            model.addAttribute("msg", "没有此类产品");
            return "err";
        }
    }
    //分类 查询更多产品
    @GetMapping("/product/list")
    public String productAll(@RequestParam("ptype") Integer productType,
                              @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "9") Integer pageSize,
                              Model model) {
        List<Product> products = new ArrayList<>();
        if (UtilParameterCheck.parameterTypeCheck(productType)) {
            pageNo = UtilParameterCheck.parameterPageNoCheck(pageNo);
            pageSize = UtilParameterCheck.parameterPageSizeCheck(pageSize);
            products = productService.queryProductInfo(productType, pageNo, pageSize);


            int countProduct = productService.queryCountProduct(productType);
            Pageinfo pageinfo = new Pageinfo(pageNo,pageSize,countProduct);
            //@TODO 投资排行榜
            //从redis中取排行榜信息
            Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeWithScores(YlbKey.INVEST_TOP_LIST, 0, 4);

            List<InvestTopList> topLists = new ArrayList<>();

            for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
                topLists.add(new InvestTopList(typedTuple.getValue(),typedTuple.getScore()));
            }

            model.addAttribute("topLists",topLists);
            model.addAttribute("pageinfo", pageinfo);
            model.addAttribute("productType", productType);
            model.addAttribute("products", products);
            return "productall";
        }else{
            model.addAttribute("err", "没有查询到此类型的产品");
            return "err";
        }

    }
}
