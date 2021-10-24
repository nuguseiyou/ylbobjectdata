package com.nuguseiyou.util;

import java.math.BigDecimal;

public class DecimalUtil {

    public static boolean ge(BigDecimal n1, BigDecimal n2) {
        boolean falg = false;
        if (n1 == null || n2 == null) {
            throw new RuntimeException("参数为null");
        }
        if (n1.compareTo(n2) >= 0) {
            falg = true;
        }
        return falg;
    }
}
