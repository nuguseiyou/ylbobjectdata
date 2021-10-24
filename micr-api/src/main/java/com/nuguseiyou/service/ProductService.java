package com.nuguseiyou.service;

import com.nuguseiyou.model.ext.PhoneBid;
import com.nuguseiyou.model.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * 2021/9/13
 */
public interface ProductService {
    /**
     * @return 查询年利率
     */
    BigDecimal queryavgrate();

    /**
     * @return 查询产品信息
     */
    List<Product> queryProductInfo(Integer type,Integer pageNo,Integer pageSize);

    /**
     * @return 查询某种类型产品总数
     */
    int queryCountProduct(Integer productType);

    /**
     * @param pid 产品id
     * @return    产品的详细信息
     */
    Product queryProductMinuteInfo(Integer pid);

}
