package com.bjpowernode.vo;

import java.io.Serializable;

//ajax请求的返回结果类
public class Result<T> implements Serializable {

    //请求是否成功 true：成功； false失败
    private boolean success;

    //错误码
    private int errcode;

    //错误消息
    private String msg;

    //应答数据
    private T data;


    public void setCodeEnum(CodeEnum codeEnum){
        this.errcode = codeEnum.getCode();
        this.msg = codeEnum.getMsg();
    }
    //默认是成功的Result
    public static Result ok(){
        Result<String> result = new Result<>();
        result.setSuccess(true);
        result.setCodeEnum(CodeEnum.RC_SUCC);
        result.setData("");
        return result;
    }

    //默认是错误的的Result
    public static Result fail(){
        Result<String> result = new Result<>();
        result.setSuccess(false);
        result.setCodeEnum(CodeEnum.RC_FAIL);
        result.setData("");
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
