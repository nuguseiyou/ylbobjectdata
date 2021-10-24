package com.bjpowernode.dataservice.service;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.contants.YLBKey;
import com.bjpowernode.dataservice.mapper.BidMapper;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.ProductMapper;
import com.bjpowernode.entity.Bid;
import com.bjpowernode.entity.FinanceAccount;
import com.bjpowernode.entity.Product;
import com.bjpowernode.entity.ext.PhoneBid;
import com.bjpowernode.entity.ext.ProudctNameBid;
import com.bjpowernode.service.InvestService;
import com.bjpowernode.util.DecimalUtil;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.CodeEnum;
import com.bjpowernode.vo.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

//投资服务
@DubboService(interfaceClass = InvestService.class,version = "1.0")
public class InvestServiceImpl implements InvestService {

    @Resource
    private BidMapper bidMapper;

    @Resource
    private FinanceAccountMapper accountMapper;

    @Resource
    private ProductMapper productMapper;


    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 投资
     * @param userId         用户id
     * @param productId   产品id
     * @param bidMoney    投资金额
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> userInvest(Integer userId, Integer productId, BigDecimal bidMoney) {

        Result<String> result = Result.fail();

        int rows  = 0;
        //1.判断金额 select * from u_finance_account ,
        FinanceAccount account = accountMapper.selectUidForUpdate(userId);
        if( account != null ){
            //比较金额
            if(DecimalUtil.ge(account.getAvailableMoney(),bidMoney)){
                //判断产品的金额
                Product product = productMapper.selectProductId(productId);
                if( product != null){
                    //判断金额 min，max ，leftMoney， status
                    if( product.getProductStatus() == YLBContants.PRODUCT_STATUS_SELL){
                        if( DecimalUtil.ge(bidMoney,product.getBidMinLimit())
                            && DecimalUtil.ge(product.getBidMaxLimit(),bidMoney)
                            && DecimalUtil.ge(product.getLeftProductMoney(),bidMoney)
                            && bidMoney.intValue() % 100 == 0 ){
                            //投资金额正确
                            //1.扣除资金余额
                            rows = accountMapper.reduceAccountAvailableMoney(userId,bidMoney);
                            if( rows < 1 ){
                                throw new RuntimeException("投资，更新资金余额失败");
                            }

                            //2.扣除产品剩余投资金额
                            rows =  productMapper.updateLeftMoney(product.getId(),bidMoney);
                            if( rows < 1){
                                throw new RuntimeException("投资，更新产品剩余可投资金额失败");
                            }

                            //3.生成投资记录
                            Bid bid  = new Bid();
                            bid.setBidMoney(bidMoney);
                            bid.setBidStatus(YLBContants.BID_STATUS_SUCC);
                            bid.setBidTime(new Date());
                            bid.setLoanId(product.getId());
                            bid.setUid(userId);
                            rows =  bidMapper.insertBidInfo(bid);
                            if(rows < 1){
                                throw new RuntimeException("投资，创建投资记录失败");
                            }

                            //判断是否产品满标
                            Product info = productMapper.selectProductId(productId);
                            if( info.getLeftProductMoney().intValue() == 0 ){
                                //更新产品记录的状态。
                                rows = productMapper.updateStatusAndFullTime(productId);
                                if(rows < 1){
                                    throw new RuntimeException("投资，更新产品的状态满标失败");
                                }
                            }
                            //投资成功
                            result = Result.ok();


                        } else {
                            //投资金额不满足要求
                            result.setCodeEnum(CodeEnum.RC_INVEST_MONEY_ERROR);
                        }

                    } else {
                        //产品不可售卖
                        result.setCodeEnum(CodeEnum.RC_PROUDCT_NOT_SELLED);
                    }
                } else {
                    //无此产品
                    result.setCodeEnum(CodeEnum.RC_NONE_PRODUCT);
                }
            } else {
                //余额不足
                result.setCodeEnum(CodeEnum.RC_ACCOUNT_MONEY_LESS);
            }
        } else {
            //没有此账户
            result.setCodeEnum(CodeEnum.RC_NONE_ACCOUNT);
        }

        return result;
    }

    /**
     * @return 总计投资金额
     */
    @Override
    public BigDecimal querySumInvestMoney() {
        ValueOperations operations = redisTemplate.opsForValue();
        BigDecimal sumInvestMoney = (BigDecimal) operations.get(YLBKey.INVEST_MONEY_SUM);
        if(sumInvestMoney == null){
            synchronized (this){
                sumInvestMoney = (BigDecimal) operations.get(YLBKey.INVEST_MONEY_SUM);
                if( sumInvestMoney == null){
                    sumInvestMoney = bidMapper.selectSumInvestMoney();
                    operations.set(YLBKey.INVEST_MONEY_SUM,sumInvestMoney,30, TimeUnit.MINUTES);
                }
            }
        }
        return sumInvestMoney;
    }

    /***
     * 产品的最近（3条）投资记录
     */
    @Override
    public List<PhoneBid> queryRecentlyBids(Integer productId) {
        List<PhoneBid>  bids = new ArrayList<>();
        if( productId != null && productId > 0 ){
            bids = bidMapper.selectBidsProduct(productId,3);
        }
        return bids;
    }

    /**
     * 查询某个用户的投资记录
     * @param uid      用户id
     * @param pageNo   页号
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public List<ProudctNameBid> queryBidListUid(Integer uid,
                                                Integer pageNo,
                                                Integer pageSize) {
        List<ProudctNameBid> list = new ArrayList<>();
        if( uid != null && uid > 0 ){
            pageNo = YLBUtil.defaultPageNo(pageNo);
            pageSize = YLBUtil.defaultPageSize(pageSize);

            list =  bidMapper.selectBidUid(uid, YLBUtil.offSet(pageNo,pageSize), pageSize);
        }
        return  list;

    }

    /**
     * 统计用户投资的总记录数
     * @param uid 用户id
     * @return
     */
    @Override
    public int queryBidCountUid(Integer uid) {
        int rows = bidMapper.selectBidCountUid(uid);
        return rows;
    }


}
