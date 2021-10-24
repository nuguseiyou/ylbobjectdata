package com.bjpowernode.service;

import com.bjpowernode.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    /**
     * @return 收益率平均值
     */
    BigDecimal queryAvgRate();


    /**
     * @param productType 产品类型（0,1,2）
     * @param pageNo      页号
     * @param pageSize    每页数据大小
     * @return  产品的List（size是0或多条数据）
     */
    List<Product> queryProuductPage(Integer productType, Integer pageNo, Integer pageSize);

    /**
     * @param productType  产品类型
     * @return 某类型产品的总记录数量
     */
    int queryTotalRecordProductType(Integer productType);

    /**
     * @param pid 产品的主键
     * @return 产品对象Product或null
     */
    Product queryProductId(Integer pid);
}
