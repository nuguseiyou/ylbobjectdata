package com.bjpowernode.micrweb.inteptor;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//登陆拦截器
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        //从session中获取user
        User user = (User) request.getSession().getAttribute(YLBContants.YLB_SESSION_USER);
        if( user == null ){
            String returnUrl = request.getRequestURL() + (request.getQueryString() == null ? "" : "?"+request.getQueryString());
            //需要登陆
            response.sendRedirect(request.getContextPath()+"/user/page/login?returnUrl="+returnUrl);
            return false;
        }
        return true;
    }
}
