package com.bjpowernode.service;

import com.bjpowernode.entity.User;

public interface UserService {

    /**
     * @return 注册用户数量
     */
    Integer queryRegisterUsers();

    /**
     * @param phone 手机号
     * @return 手机号对应的用户或者null
     */
    User queryPhone(String phone);

    /***
     * 用户注册
     */
    User userRegister(String phone,String mima);

    /**
     * 实名认证
     * @param phone  手机号
     * @param name   姓名
     * @param idCard 身份证号
     * @return
     */
    boolean realName(String phone, String name, String idCard);

    /**
     * 用户登陆
     * @param phone         手机号
     * @param loginPassword 密码
     * @return 登陆的用户或null
     */
    User userLogin(String phone, String loginPassword);
}
