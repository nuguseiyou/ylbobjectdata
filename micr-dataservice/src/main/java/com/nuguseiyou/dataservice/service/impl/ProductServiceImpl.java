package com.nuguseiyou.dataservice.service.impl;

import com.nuguseiyou.constant.YlbKey;
import com.nuguseiyou.dataservice.mapper.ProductMapper;
import com.nuguseiyou.model.ext.PhoneBid;
import com.nuguseiyou.model.Product;
import com.nuguseiyou.service.ProductService;
import com.nuguseiyou.util.UtilParameterCheck;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 2021/9/13
 */
@DubboService(interfaceClass = ProductService.class, version = "1.0")
public class ProductServiceImpl implements ProductService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ProductMapper productMapper;

    /**
     * @return 查询年利率
     */
    @Override
    public BigDecimal queryavgrate() {
        //先从redis中取数据
        ValueOperations valueOperations = redisTemplate.opsForValue();
        BigDecimal avgRate = (BigDecimal) valueOperations.get(YlbKey.PRODUCT_RATE_AVG);
        if(avgRate == null){
            synchronized (this){
                avgRate = (BigDecimal) valueOperations.get(YlbKey.PRODUCT_RATE_AVG);
                if(avgRate == null){
                    avgRate = productMapper.selectAvgRate();
                    valueOperations.set(YlbKey.PRODUCT_RATE_AVG, avgRate, 30, TimeUnit.MINUTES);
                }
            }
        }
        //BigDecimal avgRate = productMapper.selectAvgRate();
        return avgRate;
    }

    /**
     * @param type     产品类型
     * @param pageNo   页码
     * @param pageSize 页面大小
     * @return 查询产品信息
     */
    @Override
    public List<Product> queryProductInfo(Integer type, Integer pageNo, Integer pageSize) {
        List<Product> products = new ArrayList<>();
        //参数检查
        if(UtilParameterCheck.parameterTypeCheck(type)){
            pageNo = UtilParameterCheck.parameterPageNoCheck(pageNo);
            pageSize = UtilParameterCheck.parameterPageSizeCheck(pageSize);
            int offset = UtilParameterCheck.limitOffset(pageNo, pageSize);
            products = productMapper.selectProductInfo(type, offset, pageSize);
        }
        return products;
    }

    /**
     * @param productType 产品类型
     * @return            某种类型产品的总数
     */
    @Override
    public int queryCountProduct(Integer productType) {
        int CountProduct = productMapper.selectCountProduct(productType);
        return CountProduct;
    }

    /**
     * @param pid 产品id
     * @return    产品详细信息
     */
    @Override
    public Product queryProductMinuteInfo(Integer pid) {
        Product product = null;
        //参数检查
        if(pid!=null&& pid>0){
            product = productMapper.selectProductMinuteInfo(pid);
        }
        return product;
    }


}
