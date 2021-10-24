package com.bjpowernode.dataservice.service;

import com.bjpowernode.contants.YLBKey;
import com.bjpowernode.dataservice.mapper.ProductMapper;
import com.bjpowernode.entity.Product;
import com.bjpowernode.service.ProductService;
import com.bjpowernode.util.YLBUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//暴露的产品服务
@DubboService(interfaceClass = ProductService.class,version = "1.0")
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * @return 收益率平均值
     */
    @Override
    public BigDecimal queryAvgRate() {

        ValueOperations operations = redisTemplate.opsForValue();
        BigDecimal avgRate = (BigDecimal) operations.get(YLBKey.PROUDCT_RATE_AVG);

        if( avgRate == null){

            synchronized (this){
                avgRate = (BigDecimal) operations.get(YLBKey.PROUDCT_RATE_AVG);

                if( avgRate == null){
                    avgRate = productMapper.selectAvgRate();
                    operations.set(YLBKey.PROUDCT_RATE_AVG,avgRate,30, TimeUnit.MINUTES);
                }
            }

        }
        return avgRate;
    }

    /**
     * @param productType 产品类型（0,1,2）
     * @param pageNo      页号
     * @param pageSize    每页数据大小
     * @return  产品的List（size是0或多条数据）
     */
    @Override
    public List<Product> queryProuductPage(Integer productType,
                                           Integer pageNo,
                                           Integer pageSize) {
        List<Product> productList = new ArrayList<>();
        //做参数检查productType， pageNo ，pageSize
        if(YLBUtil.checkProductType(productType)){
            pageNo = YLBUtil.defaultPageNo(pageNo);
            pageSize = YLBUtil.defaultPageSize(pageSize);
            int offSet = YLBUtil.offSet(pageNo,pageSize);
            productList = productMapper.selectPage(productType,offSet, pageSize);
        }
        return productList;
    }

    /**
     * @param productType  产品类型
     * @return 某类型产品的总记录数量
     */
    @Override
    public int queryTotalRecordProductType(Integer productType) {
        int records = 0;
        if( YLBUtil.checkProductType(productType)){
            records =  productMapper.selectRecordsProductType(productType);
        }
        return records;
    }

    /**
     * @param pid 产品的主键
     * @return 产品对象Product或null
     */
    @Override
    public Product queryProductId(Integer pid) {
        Product product = null;
        if( pid != null &&  pid > 0 ){
            product = productMapper.selectProductId(pid);
        }
        return product;
    }
}
