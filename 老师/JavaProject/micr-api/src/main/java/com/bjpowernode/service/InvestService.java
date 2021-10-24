package com.bjpowernode.service;

import com.bjpowernode.entity.ext.PhoneBid;
import com.bjpowernode.entity.ext.ProudctNameBid;
import com.bjpowernode.vo.Result;

import java.math.BigDecimal;
import java.util.List;

public interface InvestService {

    /**
     * @return 总计投资总金额
     */
    BigDecimal querySumInvestMoney();

    /***
     * 产品的最近（3条）投资记录
     */
    List<PhoneBid> queryRecentlyBids(Integer productId);


    /**
     * 查询某个用户的投资记录
     * @param uid      用户id
     * @param pageNo   页号
     * @param pageSize 每页大小
     * @return
     */
    List<ProudctNameBid> queryBidListUid(Integer uid,
                                         Integer pageNo,
                                         Integer pageSize);

    /**
     * 统计用户投资的总记录数
     */
    int queryBidCountUid(Integer uid);

    /**
     * 投资
     * @param userId         用户id
     * @param productId   产品id
     * @param bidMoney    投资金额
     * @return
     */
    Result<String> userInvest(Integer userId, Integer productId, BigDecimal bidMoney);
}
