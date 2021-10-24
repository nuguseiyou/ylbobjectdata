package com.nuguseiyou;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;

/**
 * 2021/9/16
 */
public class MyTest {

    //字符串替换 用于短信验证码
    @Test
    public void test01(){
        String str = "xibalaoma%s";
        String s = String.format(str, "-------------nuguseiyou");
        System.out.println(s);
    }

    //long3时间工具类使用
    @Test
    public void test02(){
        //当前时间
        Date nowDate = new Date();
        //前一天时间 2021-09-26 00-00-00
        Date bTime = DateUtils.truncate(DateUtils.addDays(nowDate,-1), Calendar.DATE);
        System.out.println("bTime = " + bTime);
        //昨天结束时间
        Date eTime = DateUtils.truncate(nowDate,Calendar.DATE);
        System.out.println("eTime = " + eTime);
    }

    //long3 四舍五入
    @Test
    public void test03(){
        BigDecimal rate = new BigDecimal(12);
        System.out.println(rate.divide(new BigDecimal("100"), 6,RoundingMode.HALF_UP)
                .divide(new BigDecimal("360"),6,RoundingMode.HALF_UP));
        //System.out.println(rate);
    }


}
