package com.bjpowernode;

import org.junit.Test;

import java.util.regex.Pattern;

public class MyTest {

    @Test
    public void test01(){
        String phone=null;
        boolean matches = Pattern.matches("^1[1-9]\\d{9}$", phone);
        System.out.println(phone+",matches="+matches);
    }

    @Test
    public void test02(){
        String str = "【盈利宝】您的验证码是%s,请勿泄露%s";
        String res  = String.format(str,"242546","hello");
        System.out.println("res="+res);
    }
}
