package com.bjpowernode.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class YLBUtil {


    /*****************检查产品类型*************************/
    public static boolean checkProductType(Integer productType){
        boolean flag = false;
        Set<Integer> types = new HashSet<>();
        types.add(0);
        types.add(1);
        types.add(2);
        if( productType != null){
            if( types.contains(productType)){
                flag = true;
            }
        }
        return flag;
    }

    /*****************pageNo*************************/
    public static int defaultPageNo(Integer cPageNo){
        int pageNo = cPageNo;
        if( cPageNo == null || cPageNo < 1){
            pageNo =1;
        }
        return pageNo;
    }

    /*****************pageSize*************************/
    public static int defaultPageSize(Integer cPageSize){
        int pageSize = cPageSize;
        if( cPageSize == null || cPageSize < 1 || cPageSize > 50){
            pageSize = 9;
        }
        return pageSize;
    }

    /*****************计算limit 的 offset*************************/
    public static int offSet(Integer pageNo,Integer pageSize){
        return  (pageNo -1 ) * pageSize;
    }


    /*****************检查手机号格式***true:正确；false是不符合规则******************/
    public static boolean checkFormatPhone(String phone){

        boolean result = false;
        if( phone != null){
            result = Pattern.matches("^1[1-9]\\d{9}$", phone);
        }
        return result;
    }
}
