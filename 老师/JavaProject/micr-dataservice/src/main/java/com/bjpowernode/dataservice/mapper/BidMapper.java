package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.entity.Bid;
import com.bjpowernode.entity.ext.PhoneBid;
import com.bjpowernode.entity.ext.ProudctNameBid;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidMapper {

    //总计投资金额
    BigDecimal selectSumInvestMoney();

    //产品的最近投资记录
    List<PhoneBid> selectBidsProduct(@Param("productId") Integer productId, @Param("rows") Integer rows);

    //某个用户所有的投资记录
    List<ProudctNameBid> selectBidUid(@Param("uid") Integer uid,
                                      @Param("offSet") Integer offSet,
                                      @Param("rows") Integer rows);
    //查询用户投资记录的总数
    int selectBidCountUid(@Param("uid") Integer uid);


    //创建投资记录
    int insertBidInfo(Bid bid);

    //产品的所有成功的投资记录
    List<Bid> selectBidListProductId(@Param("productId") Integer productId);
}