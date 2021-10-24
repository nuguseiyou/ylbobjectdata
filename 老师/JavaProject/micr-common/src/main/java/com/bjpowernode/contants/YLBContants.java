package com.bjpowernode.contants;

public class YLBContants {

    /************产品类型*************************/
    //新手宝
    public static final  int PRODUCT_TYPE_XINSHOUBAO=0;
    //优选
    public static final  int PRODUCT_TYPE_YOUXUAN=1;
    //散标
    public static final  int PRODUCT_TYPE_SANBIAO=2;
    //注册时短信验证码有效时间，3分钟
    public static final long SMS_REG_CODE_TIME = 3 ;
    //user对象的在session中的key
    public static final String YLB_SESSION_USER ="ylb_sess_user" ;

    /************产品表的状态*************************/
    //未满标
    public static final int PRODUCT_STATUS_SELL=0;
    //满标，卖完了
    public static final int PRODUCT_STATUS_SELLED=1;
    //满标生成收益计划
    public static final int PRODUCT_STATUS_INCOME_PLAN=2;


    /************投资表的状态*************************/
    //投资成功
    public static final int BID_STATUS_SUCC=1;
    //投资失败
    public static final int BID_STATUS_FAIL=2;

    /************收益表的状态*************************/
    //收益未返还
    public static final int INCOME_STAUTS_PLAN=0;
    //收益返还
    public static final int INCOME_STAUTS_BACK=1;

    /************充值表的状态*************************/
    //充值中  0
    public static final Integer RECHARGE_STATUS_PROCESSING = 0;
    //成功
    public static final Integer RECHARGE_STATUS_SUCCESS = 1;
    //失败
    public static final Integer RECHARGE_STATUS_FAIL = 2;
    //签名错误，验签失败
    public static final Integer RECHARGE_STATUS_SIGN_FAIL = 3;
}
