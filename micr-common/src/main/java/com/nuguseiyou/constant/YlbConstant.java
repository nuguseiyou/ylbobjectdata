package com.nuguseiyou.constant;

/**
 * 2021/9/14
 */
public class YlbConstant {
    //产品类型
    /******************新手宝产品类型******************/
    public static final int XINSHOUBAO_TYPE = 0;
    /******************优选产品类型******************/
    public static final int YOUXUAN_TYPE = 1;
    /******************散标包产品类型******************/
    public static final int SANBIAO_TYPE = 2;
    /******************注册时redis中验证码存储时间******************/
    public static final long SMS_REG_CODE_TIME = 3;
    /******************user对象在session中的key******************/
    public static final String YLB_SESSION_USER = "ylb_session_user";

    //产品状态
    /******************产品未满标 可投******************/
    public static final int PRODUCT_STATUS_NOFULL = 0;
    /******************产品已满标 不可投******************/
    public static final int PRODUCT_STATUS_FULL = 1;
    /******************产品已满标并生成投资计划 不可投******************/
    public static final int PRODUCT_STATUS_INSERT_INVEST_PLAN = 2;

    //产品投资状态
    /******************产品投资成功******************/
    public static final int PRODUCT_INVEST_SUCCEED = 1;
    /******************产品投资失败******************/
    public static final int PRODUCT_INVEST_LOSE = 2;

    //收益状态
    /******************收益未返还******************/
    public static final int INVEST_INCOME_NOT_RETURN = 0;
    /******************收益已返还******************/
    public static final int INVEST_INCOME_RETURN = 1;

    //用户充值状态
    /******************充值中******************/
    public static final int USER_RECHARGE_ON = 0;
    /******************充值成功******************/
    public static final int USER_RECHARGE_TRUE = 1;
    /******************充值失败******************/
    public static final int USER_RECHARGE_FALSE = 2;
    /******************验签失败******************/
    public static final int SIGNATURE_VERIFICATION_FALSE = 3;
}
