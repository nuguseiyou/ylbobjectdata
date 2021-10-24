package com.bjpowernode.contants;

public class YLBKey {

    /**
     * redis 命名key
     * key命名：见名知其意
     * 盈利宝命名：  类别:子项:项目
     *            类别:项目
     */
    /*************首页注册用户数量*********************/
    public static final String USER_REGISTER_COUNT="USER:REG:COUNT";
    /*************首页累计成交金额*********************/
    public static final String INVEST_MONEY_SUM="INVEST:MONEY:SUM";
    /*************首页平均利率*********************/
    public static final String PROUDCT_RATE_AVG ="PRODUCT:RATE:AVG";
    /*************注册时，发送短信验证码的key**********************/
    public static final String SMS_REG_CODE = "SMS:REG:";
    /*************购买产品，生成投资排行榜**********************/
    public static final String INVEST_TOP_LIST = "INVEST:TOP:LIST";
    /*************充值的，订单号的自增序列**********************/
    public static final String RECHARGE_ORDERID_SEQ = "RECHARGE:ORDERID:SEQ";
    /*************充值的，订单号**********************/
    public static final String RECHARGE_ORDERID =  "RECHARGE:ORDERID:";
}
