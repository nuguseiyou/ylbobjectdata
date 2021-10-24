package com.bjpowernode.dataservice.service;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.dataservice.mapper.BidMapper;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.IncomeMapper;
import com.bjpowernode.dataservice.mapper.ProductMapper;
import com.bjpowernode.entity.Bid;
import com.bjpowernode.entity.Income;
import com.bjpowernode.entity.Product;
import com.bjpowernode.entity.ext.UserIncome;
import com.bjpowernode.service.IncomeService;
import com.bjpowernode.util.YLBUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//收益服务
@DubboService(interfaceClass = IncomeService.class,version = "1.0")
public class IncomeServiceImpl implements IncomeService {

    @Resource
    private IncomeMapper incomeMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private BidMapper bidMapper;

    @Resource
    private FinanceAccountMapper accountMapper;


    /**
     * 获取用户的所有收益记录
     * @param uid
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<UserIncome> queryIncomeListUid(Integer uid, Integer pageNo, Integer pageSize) {
        List<UserIncome> incomeList = new ArrayList<>();
        if( uid != null && uid > 0 ){
            pageNo = YLBUtil.defaultPageNo(pageNo);
            pageSize = YLBUtil.defaultPageSize(pageSize);
            incomeList = incomeMapper.selectIncomeListUid(uid, YLBUtil.offSet(pageNo,pageSize),pageSize);
        }
        return incomeList;
    }

    /**
     * 获取用户的收益记录总数
     * @param uid
     * @return
     */
    @Override
    public int queryIncomeCountUid(Integer uid) {
        int counts  = 0;
        if( uid != null && uid > 0 ){
            counts  = incomeMapper.selectIncomeCountUid(uid);
        }
        return counts;
    }

    //计算满标产品的收益计划
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generateIncomePlan() {

        int rows = 0;
        //计算时间
        Date curDate = new Date(); // 09-26
        Date beginTime = DateUtils.truncate(DateUtils.addDays(curDate,-1), Calendar.DATE);
        Date endTime = DateUtils.truncate(curDate,Calendar.DATE);
        //1.查询昨天满标的产品
        List<Product> productList = productMapper.selectBeforeManBiaoProudct(beginTime,endTime);

        //2.每个满标产品，查询他的投资记录
        Date incomeDate = null; //到期时间
        BigDecimal incomeMoney=null;//利息
        BigDecimal dateRate = null;//日利率
        BigDecimal cycle = null;//周期
        for(Product product: productList){
            dateRate = product.getRate().divide(new BigDecimal("100"),10, RoundingMode.HALF_UP)
                       .divide(new BigDecimal("360"),10,RoundingMode.HALF_UP);
            cycle = new BigDecimal(product.getCycle());

            List<Bid> bidList = bidMapper.selectBidListProductId(product.getId());
            //3.遍历记录，计算收益
            for(Bid bid:bidList){

                //计算利息(投资金额*周期*利率) 和 到期时间（满标的第二天开始+周期）
                Date computeBeginDate = DateUtils.addDays(product.getProductFullTime(),1);
                if( product.getProductType() == YLBContants.PRODUCT_TYPE_XINSHOUBAO){
                    incomeDate = DateUtils.addDays(computeBeginDate,product.getCycle());
                    incomeMoney = bid.getBidMoney().multiply(cycle ).multiply(dateRate);
                } else {
                    incomeDate = DateUtils.addMonths(computeBeginDate,product.getCycle());
                    incomeMoney = bid.getBidMoney().multiply( cycle.multiply( new BigDecimal("30"))).multiply(dateRate);
                }

                //创建收益记录
                Income income = new Income();
                income.setBidId(bid.getId());
                income.setBidMoney(bid.getBidMoney());
                income.setIncomeDate(incomeDate);
                income.setIncomeMoney(incomeMoney);
                income.setLoanId(product.getId());
                income.setUid(bid.getUid());
                income.setIncomeStatus(YLBContants.INCOME_STAUTS_PLAN);
                incomeMapper.insert(income);

            }

            //更新产品的状态为 满标已经生成收益计划
            rows = productMapper.updateStatus(product.getId(),YLBContants.PRODUCT_STATUS_INCOME_PLAN);
            if( rows < 1){
                throw new RuntimeException("生成收益计划，更新产品状态为2失败");
            }
        }
    }

    //收益返还
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void generateIncomeBack() {
        int rows  = 0;
        //1.查询符合条件的记录， income_date是昨天的， income_status = 0
        List<Income> incomeList = incomeMapper.selectExipreIncome();
        //2.返还收益
        for(Income income:incomeList){
            //更新用户的资金
            rows = accountMapper.updateMoneyIncomeBack(
                    income.getUid(),income.getBidMoney(),income.getIncomeMoney());

            if( rows < 1){
                throw new RuntimeException("定时任务，收益返还。更新资金余额失败");
            }

            //更新此收益记录状态为 1 （已返还）
            rows = incomeMapper.updateStatusBack(income.getId());
            if( rows < 1) {
                throw new RuntimeException("定时任务，收益返还，更新收益状态为1失败");
            }
        }
    }
}
