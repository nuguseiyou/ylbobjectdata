package com.bjpowernode.dataservice.service;

import com.bjpowernode.contants.YLBContants;
import com.bjpowernode.dataservice.mapper.FinanceAccountMapper;
import com.bjpowernode.dataservice.mapper.RechargeMapper;
import com.bjpowernode.entity.Recharge;
import com.bjpowernode.service.RechargeService;
import com.bjpowernode.util.YLBUtil;
import com.bjpowernode.vo.CodeEnum;
import com.bjpowernode.vo.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = RechargeService.class,version = "1.0")
public class RechargeServiceImpl implements RechargeService {

    @Resource
    private RechargeMapper rechargeMapper;

    @Resource
    private FinanceAccountMapper accountMapper;

    @Override
    public List<Recharge> queryListUid(Integer uid, Integer pageNo, Integer pageSize) {
        List<Recharge> rechargeList = new ArrayList<>();
        if( uid != null && uid > 0 ){
            pageNo = YLBUtil.defaultPageNo(pageNo);
            pageSize = YLBUtil.defaultPageSize(pageSize);
            rechargeList = rechargeList = rechargeMapper.selectRechargesUid(
                    uid, YLBUtil.offSet(pageNo,pageSize) , pageSize);
        }
        return rechargeList;
    }

    @Override
    public int queryCountUid(Integer uid) {
        int rows = 0;
        if( uid != null && uid > 0 ){
            rows  = rechargeMapper.selectCountUid(uid);
        }
        return rows;
    }

    //创建充值记录
    @Override
    public int addRecharge(Recharge recharge) {
        return rechargeMapper.insert(recharge);
    }

    /**
     * 充值的异步通知
     * @param orderId    商家订单号
     * @param payResult  充值结果，10：成功， 11失败
     * @param payAmount  充值金额 分为单位
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public synchronized Result  handlerRechargeNotify(String orderId,
                                        String payResult,
                                        String payAmount) {

        //加入日志处理
        Result<String> result = Result.fail();

        int rows = 0;
        //1.查询订单记录， 判断是否处理过
        Recharge recharge = rechargeMapper.selectRechargeNoLock(orderId);
        if( recharge != null){
            //2.判断充值是否处理过
            if(YLBContants.RECHARGE_STATUS_PROCESSING == recharge.getRechargeStatus()){
                //3.判断金额是否一致。
                String fen = recharge.getRechargeMoney().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();

                if(payAmount.equals(fen)){
                    //金额是一致的
                    if("10".equals(payResult)){ //充值成功
                        //4.给资金账户增加金额
                        rows = accountMapper.updateMoneyRecharge(recharge.getUid(),recharge.getRechargeMoney());
                        if(rows <1){
                            throw new RuntimeException("充值异步通知，更新账户资金失败");
                        }

                        //5.修改充值记录的状态
                        rows = rechargeMapper.updateRechargeStatus(recharge.getId(),YLBContants.RECHARGE_STATUS_SUCCESS);
                        if( rows < 1){
                            throw new RuntimeException("充值异步通知，更新充值记录的状态为成功失败");
                        }
                        //指定异步通知处理结果
                        result = Result.ok();
                        result.setCodeEnum(CodeEnum.RC_RECHAGE_SUCCESS);

                    } else {
                        //充值失败
                        rows = rechargeMapper.updateRechargeStatus(recharge.getId(),YLBContants.RECHARGE_STATUS_FAIL);
                        if(rows < 1){
                            throw new RuntimeException("充值异步通知，更新充值记录的状态为失败失败");
                        }
                        result = Result.ok();
                        result.setCodeEnum(CodeEnum.RC_RECHAGE_FAIL);
                    }

                } else {
                    //金额不一致
                    result.setCodeEnum(CodeEnum.RC_RECHAGE_MONEY_DIFFER);
                }
            } else {
                //充值记录处理过
                result.setCodeEnum(CodeEnum.RC_RECHARGE_FINISHED);
            }
        } else {
            //没有这个订单
            result.setCodeEnum(CodeEnum.RC_NOT_EXISTS_RECHARGENO);
        }
        return result;
    }

    @Override
    public boolean modifyRechargeStatus(String orderId, Integer newStatus) {
        int rows = rechargeMapper.updateRechargeStatusByRechargeNo(orderId,newStatus);
        return rows > 1;
    }

    //查询充值记录对象
    @Override
    public Recharge queryForRechargeNo(String rechargeNo) {
        Recharge recharge = rechargeMapper.selectByRechargeNo(rechargeNo);
        return recharge;
    }
}
