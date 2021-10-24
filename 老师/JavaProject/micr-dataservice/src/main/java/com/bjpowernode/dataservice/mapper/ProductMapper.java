package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.entity.Product;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface ProductMapper {

    //收益率平均值
    BigDecimal selectAvgRate();

    //分页查询产品
    List<Product> selectPage(@Param("ptype") Integer productType,
                             @Param("offSet") Integer offSet,
                             @Param("rows") Integer rows);

    //某类型产品总记录数量
    int selectRecordsProductType(@Param("ptype") Integer productType);

    /**
     * @param pid 产品的主键id
     * @return 产品对象
     */
    Product selectProductId(@Param("id") Integer pid);


    /**
     * 投资，更新产品的剩余可投资金额
     * @param productId
     * @param bidMoney
     * @return
     */
    int updateLeftMoney(@Param("id") Integer productId, @Param("bidMoney") BigDecimal bidMoney);

    /**
     * 更新产品的状态是满标 和  满标时间
     * @param productId 产品id
     * @return
     */
    int updateStatusAndFullTime(@Param("id") Integer productId);

    /**
     * 昨天的满标产品
     * @return 产品List
     */
    List<Product> selectBeforeManBiaoProudct(@Param("btime") Date beginTime, @Param("etime") Date endTime);

    /**
     * 更新某个产品的状态
     * @param id
     * @param newStatus
     * @return
     */
    int updateStatus(@Param("id") Integer id, @Param("newStatus") int newStatus);
}