package com.nuguseiyou.service;

import com.nuguseiyou.model.ext.PhoneBid;
import com.nuguseiyou.model.ext.UserBid;
import com.nuguseiyou.vo.Result;

import java.math.BigDecimal;
import java.util.List;

/**
 * 2021/9/13
 */
public interface InvestService {
    /**
     * @return 查询总成交金额
     */
    BigDecimal querySumInvestMoney();

    /**
     * @param pid 产品id
     * @return    某个产品的最近投资记录
     */
    List<PhoneBid> queryProductBidRecord(Integer pid);

    /**
     * @param uid       用户的id
     * @param pageNo    页码
     * @param pageSize  页面数据大小
     * @return          返回用户最近的投资记录集合
     */
    List<UserBid> queryUserBidRecord(Integer uid,Integer pageNo,Integer pageSize);

    /**
     * @param id 用户id
     * @return   返回用户最近投资的总记录数
     */
    int queryUserBidRecordCount(Integer id);

    /**
     *           用户投资操作
     * @param userId      用户id
     * @param productId   产品id
     * @param bidMoney    投资金额
     * @return
     */
    Result<String> userInvest(Integer userId, Integer productId, BigDecimal bidMoney);


}
