package com.nuguseiyou.dataservice.service.impl;

import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.dataservice.mapper.BidMapper;
import com.nuguseiyou.dataservice.mapper.FinanceAccountMapper;
import com.nuguseiyou.dataservice.mapper.IncomeMapper;
import com.nuguseiyou.dataservice.mapper.ProductMapper;
import com.nuguseiyou.model.Bid;
import com.nuguseiyou.model.Income;
import com.nuguseiyou.model.Product;
import com.nuguseiyou.model.ext.UserIncome;
import com.nuguseiyou.service.IncomeService;
import com.nuguseiyou.util.UtilParameterCheck;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DubboService(interfaceClass = IncomeService.class, version = "1.0")
public class IncomeServiceImpl implements IncomeService {


    @Resource
    private BidMapper bidMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private IncomeMapper incomeMapper;

    @Resource
    private FinanceAccountMapper accountMapper;

    /**
     * 查询用户最近收益记录
     *
     * @param uid      用户id
     * @param pageNo   页码
     * @param pageSize 页面数据大小
     * @return 返回用户最近收益记录集合
     */
    @Override
    public List<UserIncome> queryUserIncomeRecord(Integer uid, Integer pageNo, Integer pageSize) {
        List<UserIncome> incomes = new ArrayList<>();
        if (uid != null && uid > 0) {
            pageNo = UtilParameterCheck.parameterPageNoCheck(pageNo);
            pageSize = UtilParameterCheck.parameterPageSizeCheck(pageSize);
            incomes = incomeMapper.selectUserIncomeRecord(uid, UtilParameterCheck.limitOffset(pageNo, pageSize), pageSize);
        }
        return incomes;
    }

    /**
     * 查询用户最近收益记录数
     *
     * @param uid 用户id
     * @return 返回用户最近收益记录数
     */
    @Override
    public int queryUserIncomeRecordCount(Integer uid) {
        return incomeMapper.selectUserIncomeRecordCount(uid);
    }

    //已满标产品生成收益计划
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void queryFullProduct() {
        int num = 0;
        //当前时间
        Date nowDate = new Date();
        //前一天时间 2021-09-26 00-00-00
        Date bTime = DateUtils.truncate(DateUtils.addDays(nowDate, -1), Calendar.DATE);
        //昨天结束时间
        Date eTime = DateUtils.truncate(nowDate, Calendar.DATE);

        //查询昨天达到满标的产品
        List<Product> products = productMapper.selectFullProduct(bTime, eTime);
        Date incomeDate = null;//到期时间
        BigDecimal dayRate = null;//日利率
        BigDecimal interest = null;//利息
        for (Product product : products) {
            //日利率
            dayRate = product.getRate().divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP)
                    .divide(new BigDecimal("360"), 6, RoundingMode.HALF_UP);
            //查询满标产品的投资记录
            List<Bid> bids = bidMapper.selectFullProductInvest(product.getId());
            for (Bid bid : bids) {
                //计算利息和到期时间(利息 = 利率 * 周期 * 投资金额)
                Date expireDate = DateUtils.addDays(product.getProductFullTime(), 1);
                //新手宝产品
                if (product.getProductType() == YlbConstant.XINSHOUBAO_TYPE) {
                    //到期时间
                    incomeDate = DateUtils.addDays(expireDate, product.getCycle());
                    //利息计算
                    interest = dayRate.multiply(bid.getBidMoney()).multiply(new BigDecimal(product.getCycle()));
                } else {
                    //其他类型产品的到期时间
                    incomeDate = DateUtils.addMonths(expireDate, product.getCycle());
                    //利息计算
                    interest = dayRate.multiply(new BigDecimal(30)).multiply(bid.getBidMoney()).multiply(new BigDecimal(product.getCycle()));
                }
                //把收益与到期时间生成收益记录插入收益表
                Income income = new Income();
                income.setUid(bid.getUid());
                income.setProductId(product.getId());
                income.setBidId(bid.getId());
                income.setBidMoney(bid.getBidMoney());
                income.setIncomeDate(incomeDate);
                income.setIncomeMoney(interest);
                income.setIncomeStatus(YlbConstant.INVEST_INCOME_NOT_RETURN);
                num = incomeMapper.insertIncomeRecord(income);
                if (num < 1) {
                    throw new RuntimeException("生成投资收益记录失败");
                }
            }
            //更新产品的状态为  2: 满标已生成收益计划
            num = productMapper.updateProductStatusIncome(product.getId(),YlbConstant.PRODUCT_STATUS_INSERT_INVEST_PLAN);
            if (num < 1) {
                throw new RuntimeException("修改产品状态为 '2: 满标已生成收益计划' 失败");
            }
        }
    }

    //到期收益返还
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void incomeReturn(){
        int num = 0;

        //查询昨天到期的收益记录 and income_status = 0
        List<Income> incomes = incomeMapper.selectExpireIncomeRecord(YlbConstant.INVEST_INCOME_NOT_RETURN);
        for (Income income : incomes) {
            //把利息与本金返还到用户资金余额
            num = accountMapper.updateAvailableForIncomeReturn(income.getUid(),income.getIncomeMoney(),income.getBidMoney());
            if(num < 1){
                throw new RuntimeException("收益返还 将收益返还到用户资金余额失败");
            }
            //修改收益记录的收益状态为 1 表示已返还
            num = incomeMapper.updateIncomeStatus(income.getId(),YlbConstant.INVEST_INCOME_RETURN);
            if(num < 1){
                throw new RuntimeException("收益返还 修改收益记录的收益状态为已返还 失败");
            }
        }

    }
}
