package com.nuguseiyou.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 2021/9/14
 */
public class UtilParameterCheck {
    //检查产品类型是否存在
    public static boolean parameterTypeCheck(Integer productType){
        boolean flag = false;
        Set<Integer> typeset = new HashSet<>();
        typeset.add(0);
        typeset.add(1);
        typeset.add(2);
        if (productType != null) {
            if(typeset.contains(productType)){
                flag = true;
            }
        }
        return flag;
    }

    //检查页码参数
    public static int parameterPageNoCheck(Integer cPageNo){
        int pageNo = cPageNo;
        if(cPageNo == null || cPageNo < 1){
            pageNo = 1;
        }
        return pageNo;
    }

    //检查页码参数
    public static int parameterPageSizeCheck(Integer cPageSize){
        int pageSize = cPageSize;
        if(cPageSize == null || cPageSize < 1 || cPageSize > 50){
            pageSize = 9;
        }
        return pageSize;
    }

    //计算limit的offset
    public static int limitOffset(Integer pageNo,Integer pageSize){
        return (pageNo - 1) * pageSize;
    }

    //手机号格式验证
    public static boolean checkPhoneFormat(String phone){
        boolean flag = false;
        if(phone != null){
            flag = Pattern.matches("^1[1-9]\\d{9}$", phone);
        }
        return flag;
    }

}
