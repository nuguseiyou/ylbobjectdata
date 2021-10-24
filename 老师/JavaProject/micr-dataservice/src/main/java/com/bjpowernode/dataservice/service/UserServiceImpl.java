package com.bjpowernode.dataservice.service;

import com.bjpowernode.contants.YLBKey;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.UserMapper;
import com.bjpowernode.entity.FinanceAccount;
import com.bjpowernode.entity.User;
import com.bjpowernode.service.UserService;
import com.bjpowernode.util.YLBUtil;
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

/***********暴露dubbo服务*************/
@DubboService(interfaceClass = UserService.class,version = "1.0")
public class UserServiceImpl implements UserService {

    @Value("${mima.salt}")
    private String mimaSalt;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FinanceAccountMapper accountMapper;


    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @return 注册用户总数
     * 1.从redis查询注册用户数
     * 2.如果redis没有，从数据库查询。把结果数据存放redis
     * 3.使用的redis的string类型
     */
    @Override
    public  Integer queryRegisterUsers() {
        //1.从redis获取数据
        ValueOperations operations = redisTemplate.opsForValue();
        Integer registerUsers = (Integer) operations.get(YLBKey.USER_REGISTER_COUNT);

        //2.判断是否获取数据
        if( registerUsers == null ){
            //同步 查询数据库，存放redis
            synchronized (this){
                //再次查询redis
                registerUsers = (Integer) operations.get(YLBKey.USER_REGISTER_COUNT);
                if( registerUsers == null){
                    //3.从数据库查询数据
                    registerUsers  = userMapper.selectRegisterUsers();
                    //4.把数据存放到redis，设置30分钟有效
                    operations.set(YLBKey.USER_REGISTER_COUNT,registerUsers,30, TimeUnit.MINUTES);
                }
            }
        }
        return registerUsers;
    }

    /**
     * @param phone 手机号
     * @return 手机号对应的用户或者null
     */
    @Override
    public User queryPhone(String phone) {
        User user = null;
        //检查手机号的格式
        if(YLBUtil.checkFormatPhone(phone)){
            //调用数据库的查询（）
            user = userMapper.selectPhone(phone);

        }
        return user;
    }

    /***
     * 用户注册
     * @param phone 手机号
     * @param mima  客户端传递过来的md5加密后的密码值
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public User userRegister(String phone, String mima) {

        //1.查询手机号是否注册过
        User user = userMapper.selectPhone(phone);
        if( user == null){
            //手机号没有注册过，可以注册
            //1.添加 u_user
            user = new User();
            user.setPhone(phone);
            //对参数值做二次md5（）加密
            mima = DigestUtils.md5Hex(mima+mimaSalt);
            user.setLoginPassword(mima);
            user.setAddTime(new Date());
            user.setSource("regist");
            userMapper.insertUserReturnId(user);

            //2.添加 account ，初始金额 888
            FinanceAccount account = new FinanceAccount();
            account.setUid(user.getId());
            account.setAvailableMoney(new BigDecimal("888"));
            accountMapper.insert(account);

        } else {
            user.setSource("exists");
        }
        return user;
    }

    /**
     * 实名认证
     * @param phone  手机号
     * @param name   姓名
     * @param idCard 身份证号
     * @return true:更新成功；false失败
     */
    @Override
    public boolean realName(String phone, String name, String idCard) {
        int rows  = userMapper.updateRealName(phone,name,idCard);
        return rows > 0;
    }

    /**
     * 用户登陆
     * @param phone         手机号
     * @param loginPassword 密码
     * @return 登陆的用户或null
     */
    @Override
    public User userLogin(String phone, String loginPassword) {

        User user = null;
        if( YLBUtil.checkFormatPhone(phone ) && loginPassword != null){
            //调用数据库的操作，查询用户名和密码
            String password =  DigestUtils.md5Hex(loginPassword+mimaSalt);
            user  = userMapper.selectLogin(phone, password);
            if( user != null){
                //更新登陆的时间
                userMapper.updateLoginTime(user.getId(), new Date());
            }
        }
        return user;
    }
}
