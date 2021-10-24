package com.nuguseiyou.dataservice.mapper;

import com.nuguseiyou.model.Bid;
import com.nuguseiyou.model.ext.PhoneBid;
import com.nuguseiyou.model.ext.UserBid;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BidMapper {

    /**
     * @return 总成交金额
     */
    BigDecimal selectSumInvestMoney();

    /**
     * @param pid 产品id
     * @return    某个产品的最近投资记录
     */
    List<PhoneBid> selectProductBidRecord(@Param("pid") Integer pid, @Param("rows") Integer rows);

    /**
     * @param uid       用户的id
     * @param offset    数据截取的起始位置
     * @param rows      页面数据大小
     * @return          返回用户最近的投资记录集合
     */
    List<UserBid> selectUserBidRecord(@Param("uid") Integer uid, @Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * @param uid 用户id
     * @return   返回用户最近投资的总记录数
     */
    int selectUserBidRecordCount(Integer uid);

    //添加用户投资记录
    int insertInvestRecord( Bid bid);

    //查询满标产品的投资记录
    List<Bid> selectFullProductInvest(@Param("id") Integer id);
}