package com.bjpowernode.util;

import java.math.BigDecimal;

public class DecimalUtil {

    //比较BigDecimal

    /***
     * 比较BigDecimal
     * @param n1
     * @param n2
     * @return true: n1>=n2 ; false:其他
     */
    public static boolean ge(BigDecimal n1,BigDecimal n2){
        boolean flag = false;
        if( n1 == null || n2 == null){
            throw new RuntimeException("参数为null");
        }
        if( n1.compareTo(n2) >=0 ){
            flag = true;
        }
        return flag;

    }
}
