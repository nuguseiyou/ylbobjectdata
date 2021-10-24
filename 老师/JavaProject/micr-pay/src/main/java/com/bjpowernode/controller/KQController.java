package com.bjpowernode.controller;

import com.bjpowernode.service.KuaiQianService;
import com.bjpowernode.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;

@Controller
public class KQController {

    @Resource
    private KuaiQianService kuaiQianService;

    //rechargeMoney=1&rechargeNo=202109281044288712&userId=1
    @RequestMapping("/kq/recvKqRequestForWeb")
    public String recvKQRequestForMicrWeb(String rechargeMoney,
                                          String rechargeNo,
                                          Integer userId,
                                          Model model) {
        //数据检查处理
        if (rechargeMoney != null && !rechargeMoney.equals("")
                && rechargeNo != null && rechargeNo.length() > 0
                && userId != null && userId > 0) {

            //生成form数据
            Map<String, String> param = kuaiQianService.generateKqFormData(userId, rechargeMoney, rechargeNo);
            model.addAllAttributes(param);

            return "kqForm"; //视图

        }

        return "error"; //视图
    }


    //接收快钱的异步通知
    @GetMapping("/kq/notify")
    public void kuaiQianNotify(HttpServletRequest request,HttpServletResponse response) {

        try {
            StringBuffer buffer = new StringBuffer("");
            Enumeration<String> parameterNames = request.getParameterNames();
            while( parameterNames.hasMoreElements()){
                String name = parameterNames.nextElement();
                String value = request.getParameter(name);
                buffer.append(name).append("=").append(value).append("&");
            }
            System.out.println("接收的参数集合："+ buffer.toString());

            //快钱异步通知的处理方法
            kuaiQianService.handlerKQNotify(request);
        } finally {
            try {
                response.setContentType("text/html;charset=utf-8");
                PrintWriter out = response.getWriter();
                out.println("<result>1</result><redirecturl>http://localhost:8000/ylb/user/page/mycenter</redirecturl>");
                out.flush();
                out.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    //接收micr-web服务发送获取的查询快钱支付订单状态的请求
    @GetMapping("/kq/queryOrderForWeb")
    @ResponseBody
    public Result kuaiQianOrderForWeb(String rechargeNo){
        return kuaiQianService.invokeKqOrderQuery(rechargeNo);
    }
}
