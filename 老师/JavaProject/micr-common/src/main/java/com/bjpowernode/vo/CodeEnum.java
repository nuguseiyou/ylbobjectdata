package com.bjpowernode.vo;

public enum  CodeEnum {
    RC_SUCC(0,"成功"),
    RC_FAIL(1,"未知错误"),
    RC_FORMAT_ERROR(2,"格式错误"),
    RC_EXITS_PHONE(3,"请更换注册的手机号"),
    RC_FORMAT_PHONE_ERROR(4,"手机号格式不正确"),
    RC_AUTHCODE_ERROR(5,"验证码无效"),
    RC_NAME_ERROR(6,"姓名不正确"),
    RC_PHONE_INVLIDATE(7,"认证手机号不可用"),
    RC_PHONE_PASSWORD_INVLIDATE(8,"手机号或者密码无效"),
    RC_NO_REALNAME(9,"没有实名认证"),
    RC_INVEST_MONEY_ERROR(10,"投资金额不满足要求"),
    RC_PROUDCT_NOT_SELLED(11,"产品不可售卖"),
    RC_NONE_PRODUCT(12,"无此产品"),
    RC_ACCOUNT_MONEY_LESS(13,"余额不足"),
    RC_NONE_ACCOUNT(14,"没有此账户"),
    RC_NOT_EXISTS_RECHARGENO(15,"没有此充值记录"),
    RC_RECHARGE_FINISHED(16,"充值记录已经处理完成"),
    RC_RECHAGE_MONEY_DIFFER(17,"充值金额不一致"),
    RC_RECHAGE_SUCCESS(18,"充值异步通知，充值结果是成功"),
    RC_RECHAGE_FAIL(19,"充值异步通知，充值结果是失败"),
    RC_RECHAGE_SIGN_FAIL(20,"充值异步通知，验签失败"),

    ;

    private CodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    //错误码
    private int code;
    //code描述
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
    }
}
