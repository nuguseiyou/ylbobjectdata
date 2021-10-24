package com.nuguseiyou.test;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MyTest {


    @Test
    public void test01(){
        String money = "0.01";

        System.out.println(new BigDecimal(money).multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString()  );
    }
}
