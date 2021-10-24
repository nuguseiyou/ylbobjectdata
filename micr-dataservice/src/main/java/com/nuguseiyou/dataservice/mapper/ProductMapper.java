package com.nuguseiyou.dataservice.mapper;

import com.nuguseiyou.model.Product;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ProductMapper {
    /**
     * @return 查询年利率
     */
    BigDecimal selectAvgRate();

    /**
     * @param type     产品类型
     * @param offset   页码
     * @param rows     页面大小
     * @return         查询产品信息
     */
    List<Product> selectProductInfo(@Param("type") Integer type,
                                    @Param("offset") Integer offset,
                                    @Param("rows") Integer rows);

    /**
     * @return 查询某种类型产品总数
     */
    int selectCountProduct(@Param("ptype") Integer productType);

    /**
     * @param pid 产品编号
     * @return    产品详细信息
     */
    Product selectProductMinuteInfo(@Param("pid") Integer pid);

    //扣除产品剩余可投资金额
    int updateLeftProductMoney(@Param("productId") Integer productId, @Param("bidMoney") BigDecimal bidMoney);

    //修改产品状态为已满标
    int updateProductStatus(@Param("productId") Integer productId);

    //查询昨天达到满标的产品
    List<Product> selectFullProduct(@Param("bTime") Date bTime , @Param("eTime") Date eTime);

    //修改产品状态为  2: 满标已生成收益计划
    int updateProductStatusIncome(@Param("productId") Integer id, @Param("productStatus") int productStatus);



}