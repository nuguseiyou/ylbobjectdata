package com.nuguseiyou.vo;

/**
 * 2021/9/16
 */
public enum CodeEnum {
    RC_SUCC(0,"成功"),
    RC_FAIL(1,"失败"),
    RC_FORMAT_ERROR(2,"格式错误"),
    RC_EDITS_PHONE(3,"请更换手机号"),
    RC_SMS_CODE_ERROR(4,"短信验证码错误"),
    RC_PHONE_FORMAT_ERROR(5,"手机号格式错误"),
    RC_REALNAME_FORMAT_ERROR(6,"姓名格式错误"),
    RC_PHONE_ERROR(7,"手机号错误"),
    RC_PHONE_PWD_ERROR(8,"手机号或者密码错误"),
    RC_PHONE_PWD_NULL(9,"手机号和密码都不能为空"),
    RC_NO_REALNAME(10,"请先进行实名认证"),
    RC_INVEST_MONEY_ERROR(11,"投资金额不满足条件"),
    RC_INVEST_PRODUCT_FULL(12,"产品已满标 不可投"),
    RC_INVEST_PRODUCT_NOT_EXIST(13,"不存在该产品"),
    RC_BID_NOT_SUFFICIENT_FUNDS(14,"账户余额不足"),
    RC_BID_NOT_EXIST(14,"账户不存在"),
    RC_RECHARGE_ORDER_NO_EXIST(15,"订单不存在"),
    RC_RECHARGE_ORDER_SUCCESS(16,"该订单已经处理完成"),
    RC_RECHARGE_ORDER_MONEY_NOEQUALITY(17,"充值金额不相等"),
    RC_RECHARGE_ORDER_PAYRESULT_NOEQUALITY(18,"充值payResult不一致"),
    RC_RECHARGE_SUCCEED(19,"充值异步通知 充值结果时成功"),
    RC_RECHARGE_NOTHING(20,"充值异步通知 充值结果是失败"),
    RC_SIGNATURE_VERIFICATION_FULL(21,"充值异步通知 验签失败"),
    ;

    private CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;

    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }}
