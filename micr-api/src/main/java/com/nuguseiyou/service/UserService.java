package com.nuguseiyou.service;

import com.nuguseiyou.model.User;

/**
 * 2021/9/13
 */
public interface UserService {

    /**
     * @return 查询注册用户总数
     */
    Integer queryRegisterUsers();

    /**
     * @param phone 手机号
     * @return      查询手机号是否已经注册
     */
    User queryPhone(String phone);

    /**
     * @param phone 用户注册的手机号
     * @param mima  用户密码
     * @return      注册的方法
     */
    User userRegister(String phone, String mima);

    /**
     * @param phone     手机号
     * @param name      姓名
     * @param idCard    身份证号
     * @return          根据唯一标识手机号来修改用户信息
     */
    boolean editUserInfo(String phone,String name,String idCard);

    /**
     *              查询是否存在该用户
     * @param phone 用户手机号
     * @param mima  登录密码
     * @return
     */
    User userLogin(String phone, String mima);
}
