package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface UserMapper {

    /******统计注册用户总数*********/
    int selectRegisterUsers();

    /******查询手机号对应的用户******************/
    User selectPhone(@Param("phone") String phone);

    /*******注册用户，并获取主键id**************************/
    int insertUserReturnId(User user);

    /********更新实名认证的信息******************************/
    int updateRealName(@Param("phone") String phone, @Param("name") String name, @Param("idCard") String idCard);

    /********用户登陆******************************/
    User selectLogin(@Param("phone") String phone, @Param("mima") String password);

    /********更新用户的最近登陆时间******************************/
    int updateLoginTime(@Param("id") Integer id, @Param("loginTime") Date date);
}