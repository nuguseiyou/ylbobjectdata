package com.nuguseiyou.controller;

import com.nuguseiyou.service.KqService;
import com.nuguseiyou.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

@Controller
public class KqController {

    @Resource
    private KqService kqService;

    //调用快钱的支付接口 用form表单传参
    //rechargeMoney=0.01&orderId=202109281924445796&userId=1
    @GetMapping("/kqRuquestUrl")
    public String kqRequestUrlWeb(Integer userId, String rechargeMoney, String orderId, Model model){
        //参数验证
        if(userId != null && userId > 0
                && !rechargeMoney.equals("") && rechargeMoney != null
                && orderId != null && orderId.length() > 0){

            Map<String, String> stringStringMap = kqService.generateKqFormData(userId, rechargeMoney, orderId);
            model.addAllAttributes(stringStringMap);
        }

        return "Pkipair";
    }
    
    
    //接收异步通知
    @GetMapping("/kq/notify")
    public void receiveAsyncInform(HttpServletRequest request,HttpServletResponse response){
        try {
            StringBuilder builder = new StringBuilder("");
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()){
                String name = parameterNames.nextElement();
                String value = request.getParameter(name);
                builder.append("参数名= " + name + "参数值= " + value);
            }
            System.out.println("builder = " + builder);
            //验证参数与验签
            kqService.SignatureVerification(request);
        } finally {
            try {
                response.setContentType("text/html; charset=utf-8");
                PrintWriter writer = response.getWriter();
                writer.println("<result>1</result> <redirecturl>http://localhost:8080/user/page/centre</redirecturl>");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //接收web服务的请求 get
    //调用快钱查询接口 查询交易订单的状态 (订单补偿机制)
    @GetMapping("/kq/kqQueryUrl")
    @ResponseBody
    public Result orderQuery(String orderId){
        //调用快钱查询接口
        return kqService.kqQueryOrderService(orderId);
    }
}
