package com.nuguseiyou.dataservice.service.impl;

import com.nuguseiyou.constant.YlbKey;
import com.nuguseiyou.dataservice.mapper.FinanceAccountMapper;
import com.nuguseiyou.dataservice.mapper.UserMapper;
import com.nuguseiyou.model.FinanceAccount;
import com.nuguseiyou.model.User;
import com.nuguseiyou.service.UserService;
import com.nuguseiyou.util.UtilParameterCheck;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 2021/9/13
 */
@DubboService(interfaceClass = UserService.class, version = "1.0")
public class UserServiceImpl implements UserService {

    @Value("${mima.salt}")
    private String salt;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserMapper userMapper;

    /**
     * @return 查询注册用户总数
     */
    @Override
    public Integer queryRegisterUsers() {
        //从redis中取数据
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer registerUsers = (Integer) valueOperations.get(YlbKey.USER_REGISTER_COUNT);

        if (registerUsers == null) {
            synchronized (this) {
                registerUsers = (Integer) valueOperations.get(YlbKey.USER_REGISTER_COUNT);
                if (registerUsers == null) {
                    registerUsers = userMapper.selectRegisterUsers();
                    valueOperations.set(YlbKey.USER_REGISTER_COUNT, registerUsers, 30, TimeUnit.MINUTES);
                }
            }
        }
        //Integer registerUsers = userMapper.selectRegisterUsers();
        return registerUsers;
    }

    /**
     * @param phone 手机号
     * @return 查询手机号是否已经注册
     */
    @Override
    public User queryPhone(String phone) {
        User user = null;
        if (UtilParameterCheck.checkPhoneFormat(phone)) {
            user = userMapper.selectPhone(phone);
        }
        return user;
    }

    /**
     * @param phone 用户注册的手机号
     * @param mima  前端传来经过MD5加密后的密码
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User userRegister(String phone, String mima) {
        //先查询手机号是否注册过
        User user = userMapper.selectPhone(phone);
        if (user == null) {
            user = new User();
            user.setAddTime(new Date());
            user.setPhone(phone);
            //将密码加盐值进行二次加密
            mima = DigestUtils.md5Hex(mima + salt);
            user.setLoginPassword(mima);
            user.setSources("register");
            userMapper.insertUser(user);
            //向新注册用户账户添加888金额
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(user.getId());
            financeAccount.setAvailableMoney(new BigDecimal(888));
            financeAccountMapper.insert(financeAccount);
        } else {
            user.setSources("existUser");
        }
        return user;
    }

    /**
     * 根据唯一标识手机号来修改用户信息
     *
     * @param phone  手机号
     * @param name   姓名
     * @param idCard 身份证号
     * @return
     */
    @Override
    public boolean editUserInfo(String phone, String name, String idCard) {
        int i = userMapper.updateUserInfo(phone, name, idCard);
        return i > 0;
    }


    /**
     * 查询是否存在该用户
     *
     * @param phone 用户手机号
     * @param mima  登录密码
     * @return
     */
    @Override
    public User userLogin(String phone, String mima) {
        User user = null;
        //将密码加盐值进行二次加密
        mima = DigestUtils.md5Hex(mima + salt);
        if (UtilParameterCheck.checkPhoneFormat(phone) && mima.length() == 32) {
            user = userMapper.selectUser(phone, mima);
            if (user != null) {
                //如果用户存在就去修改用户最后的登陆时间
                userMapper.updateUserLastLoginTime(user.getId(),new Date());
            }
        }
        return user;
    }
}
