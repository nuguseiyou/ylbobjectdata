package com.nuguseiyou.dataservice.mapper;

import com.nuguseiyou.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface UserMapper {
    /**
     * @return
     * 查询注册用户总数
     */
    Integer selectRegisterUsers();

    /**
     * @param phone 手机号
     * @return      查询手机号是否已经注册
     */
    User selectPhone(@Param("phone") String phone);

    /**
     * @param user 创建一个用户
     * @return
     */
    int insertUser(User user);

    /**
     * @param phone     手机号
     * @param name      姓名
     * @param idCard    身份证号
     * @return          根据唯一标识手机号来修改用户信息
     */
    int updateUserInfo(@Param("phone") String phone, @Param("name") String name, @Param("idCard") String idCard);

    /**
     *              查询是否存在该用户
     * @param phone 用户手机号
     * @param mima  登录密码
     * @return
     */
    User selectUser(@Param("phone") String phone, @Param("mima") String mima);


    /**
     * @param id    用户id
     * @param date  获取当前时间
     * @return
     */
    int updateUserLastLoginTime(@Param("id") Integer id, @Param("lastLoginTime") Date date);
}