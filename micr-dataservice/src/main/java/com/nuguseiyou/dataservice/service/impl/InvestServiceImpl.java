package com.nuguseiyou.dataservice.service.impl;

import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.constant.YlbKey;
import com.nuguseiyou.dataservice.mapper.BidMapper;
import com.nuguseiyou.dataservice.mapper.FinanceAccountMapper;
import com.nuguseiyou.dataservice.mapper.IncomeMapper;
import com.nuguseiyou.dataservice.mapper.ProductMapper;
import com.nuguseiyou.model.Bid;
import com.nuguseiyou.model.FinanceAccount;
import com.nuguseiyou.model.Income;
import com.nuguseiyou.model.Product;
import com.nuguseiyou.model.ext.PhoneBid;
import com.nuguseiyou.model.ext.UserBid;
import com.nuguseiyou.service.InvestService;
import com.nuguseiyou.util.DecimalUtil;
import com.nuguseiyou.util.UtilParameterCheck;
import com.nuguseiyou.vo.CodeEnum;
import com.nuguseiyou.vo.Result;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 2021/9/13
 */
@DubboService(interfaceClass = InvestService.class, version = "1.0")
public class InvestServiceImpl implements InvestService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private BidMapper bidMapper;

    @Resource
    private FinanceAccountMapper accountMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private IncomeMapper incomeMapper;

    /**
     * @return 查询总成交金额
     */
    @Override
    public BigDecimal querySumInvestMoney() {
        //先从redis中取数据
        ValueOperations valueOperations = redisTemplate.opsForValue();
        BigDecimal investMoney = (BigDecimal) valueOperations.get(YlbKey.INVEST_MONEY_SUM);

        if (investMoney == null) {
            synchronized (this) {
                investMoney = (BigDecimal) valueOperations.get(YlbKey.INVEST_MONEY_SUM);
                if (investMoney == null) {
                    investMoney = bidMapper.selectSumInvestMoney();
                    valueOperations.set(YlbKey.INVEST_MONEY_SUM, investMoney, 30, TimeUnit.MINUTES);
                }
            }
        }
        //BigDecimal investMoney = bidMapper.selectSumInvestMoney();
        return investMoney;
    }

    /**
     * @param pid 产品id
     * @return 某个产品的最近投资记录集合
     */
    @Override
    public List<PhoneBid> queryProductBidRecord(Integer pid) {
        List<PhoneBid> phoneBids = new ArrayList<>();
        if (pid != null && pid > 0) {
            phoneBids = bidMapper.selectProductBidRecord(pid, 3);
        }
        return phoneBids;
    }

    /**
     * @param uid      用户的id
     * @param pageNo   页码
     * @param pageSize 页面数据大小
     * @return 返回用户最近的投资记录集合
     */
    @Override
    public List<UserBid> queryUserBidRecord(Integer uid, Integer pageNo, Integer pageSize) {
        List<UserBid> userBids = new ArrayList<>();
        if (uid != null && uid > 0) {
            pageNo = UtilParameterCheck.parameterPageNoCheck(pageNo);
            pageSize = UtilParameterCheck.parameterPageSizeCheck(pageSize);
            userBids = bidMapper.selectUserBidRecord(uid, UtilParameterCheck.limitOffset(pageNo, pageSize), pageSize);
        }
        return userBids;
    }

    /**
     * @param uid 用户id
     * @return 返回用户最近投资的总记录数
     */
    @Override
    public int queryUserBidRecordCount(Integer uid) {
        return bidMapper.selectUserBidRecordCount(uid);
    }


    /**
     * 用户投资操作
     *
     * @param userId    用户id
     * @param productId 产品id
     * @param bidMoney  投资金额
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<String> userInvest(Integer userId, Integer productId, BigDecimal bidMoney) {
        Result result = Result.fail();
        int num;
        //查询资金账户并上锁
        FinanceAccount account = accountMapper.selectMoneyForUpdate(userId);
        if (account != null) {
            //账户存在可以投资
            if (DecimalUtil.ge(account.getAvailableMoney(), bidMoney)) {
                //账户金额大于等于投资金额
                //查询产品是否存在
                Product product = productMapper.selectProductMinuteInfo(productId);
                if (product != null) {
                    //该产品存在
                    if (product.getProductStatus() == YlbConstant.PRODUCT_STATUS_NOFULL) {
                        //产品未满标 可投
                        if (DecimalUtil.ge(bidMoney, product.getBidMinLimit())
                                && DecimalUtil.ge(product.getBidMaxLimit(), bidMoney)
                                && DecimalUtil.ge(product.getLeftProductMoney(), bidMoney)
                                && bidMoney.intValue() % 100 == 0) {
                            //投资金额正确
                            //1 先扣除用户资金表余额
                            num = accountMapper.updateAvailable(userId, bidMoney);
                            if (num < 1) {
                                //用户资金表余额更新失败
                                throw new RuntimeException("投资,更新资金表余额失败");
                            }
                            //2 扣除产品的剩余可投资金额
                            num = productMapper.updateLeftProductMoney(productId, bidMoney);
                            if (num < 1) {
                                //产品剩余可投资金额更新失败
                                throw new RuntimeException("投资,更新产品表剩余额投资金额失败");
                            }
                            //3 生成投资记录
                            Bid bid = new Bid();
                            bid.setUid(userId);
                            bid.setBidMoney(bidMoney);
                            bid.setProductId(productId);
                            bid.setBidTime(new Date());
                            bid.setBidStatus(YlbConstant.PRODUCT_INVEST_SUCCEED);
                            num = bidMapper.insertInvestRecord(bid);
                            if (num < 1) {
                                throw new RuntimeException("投资,添加用户投资记录失败");
                            }
                            //4 判断产品是否满标
                            if (product.getLeftProductMoney().intValue() == 0) {
                                //修改产品状态为已满标
                                num = productMapper.updateProductStatus(productId);
                                if (num < 1) {
                                    throw new RuntimeException("投资,修改产品状态为已满标失败");
                                }
                            }
                            result = Result.ok();
                        } else {
                            //投资金额不满足条件
                            result.setCodeEnum(CodeEnum.RC_INVEST_MONEY_ERROR);
                        }
                    } else {
                        //产品已满标 不可投
                        result.setCodeEnum(CodeEnum.RC_INVEST_PRODUCT_FULL);
                    }
                } else {
                    //不存在该产品
                    result.setCodeEnum(CodeEnum.RC_INVEST_PRODUCT_NOT_EXIST);
                }
            } else {
                //账户余额不足
                result.setCodeEnum(CodeEnum.RC_BID_NOT_SUFFICIENT_FUNDS);
            }
        } else {
            //账户不存在
            result.setCodeEnum(CodeEnum.RC_BID_NOT_EXIST);
        }
        return result;
    }


}
