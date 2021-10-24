package com.bjpowernode;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import java.math.BigDecimal;

public class MyTest {
    @Test
    public void test01(){
        String s = DigestUtils.md5Hex("e0c10f451217b93f76c2654b2b729b85AAA");
        System.out.println("s="+s);

        //e0c10f451217b93f76c2654b2b729b85
        //e0c10f451217b93f76c2654b2b729b85
    }

    @Test
    public void test02(){
        String money="100.01";
        String s = new BigDecimal(money).multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();
        System.out.println("s = " + s);



    }
}
