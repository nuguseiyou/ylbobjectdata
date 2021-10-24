package com.nuguseiyou.constant;

/**
 * 2021/9/15
 */
public class YlbKey {
    /******************总注册用户量******************/
    public static final String USER_REGISTER_COUNT = "USER_REG_COUNT";
    /******************总成交金额******************/
    public static final String INVEST_MONEY_SUM = "INVEST_MONEY_SUM";
    /******************产品总平均利率******************/
    public static final String PRODUCT_RATE_AVG = "PRODUCT_RATE_AVG";
    /******************注册时短信验码在redis中的key*******/
    public static final String SMS_REG_CODE = "SMS:REG:";
    /******************投资排行榜在redis中的key*******/
    public static final String INVEST_TOP_LIST = "INVEST_TOP_LIST";
    /******************redis中自增序列*******/
    public static final String RECHARGE_ORDER_SEQ = "RECHARGE:ORDER:SEQ:";
    /******************redis中存储的充值订单号*******/
    public static final String RECHARGE_ORDER = "RECHARGE:ORDER:";
}
