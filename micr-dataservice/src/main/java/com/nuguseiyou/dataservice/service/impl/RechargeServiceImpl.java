package com.nuguseiyou.dataservice.service.impl;

import com.nuguseiyou.constant.YlbConstant;
import com.nuguseiyou.dataservice.mapper.FinanceAccountMapper;
import com.nuguseiyou.dataservice.mapper.RechargeMapper;
import com.nuguseiyou.model.Recharge;
import com.nuguseiyou.service.RechargeService;
import com.nuguseiyou.util.UtilParameterCheck;
import com.nuguseiyou.vo.CodeEnum;
import com.nuguseiyou.vo.Result;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DubboService(interfaceClass = RechargeService.class, version = "1.0")
public class RechargeServiceImpl implements RechargeService {

    @Resource
    private RechargeMapper rechargeMapper;

    @Resource
    private FinanceAccountMapper accountMapper;

    /**
     * 查询用户最近充值记录
     *
     * @param uid      用户id
     * @param pageNo   页码
     * @param pageSize 页面数据大小
     * @return 返回用户最近充值记录集合
     */
    @Override
    public List<Recharge> queryUserRechargeRecord(Integer uid, Integer pageNo, Integer pageSize) {
        List<Recharge> recharges = new ArrayList<>();
        if (uid != null && uid > 0) {
            pageNo = UtilParameterCheck.parameterPageNoCheck(pageNo);
            pageSize = UtilParameterCheck.parameterPageSizeCheck(pageSize);
            recharges = rechargeMapper.selectUserRechargeRecord(uid, UtilParameterCheck.limitOffset(pageNo, pageSize), pageSize);
        }
        return recharges;
    }

    /**
     * @param uid 用户id
     * @return 返回用户最近充值记录数
     */
    @Override
    public int queryUserRechargeRecordCount(Integer uid) {
        return rechargeMapper.selectUserRechargeRecordCount(uid);
    }

    //创建用户充值记录
    @Override
    public int addRecharge(Recharge recharge) {
        return rechargeMapper.insertRecharge(recharge);
    }

    //异步充值通知
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result AsyncRechargeNotify(String orderId, String payResult, String payAmount) {
        int num = 0;
        Result result = null;
        //根据订单号查询订单是否存在
        Recharge recharge = rechargeMapper.selectRecharge(orderId);
        if (recharge != null) {
            //订单存在 判断订单的状态 订单有没有被处理过
            if (recharge.getRechargeStatus() == YlbConstant.USER_RECHARGE_ON) {
                //该订单还在充值中   判断充值金额是否一致
                String rechargeMoney = recharge.getRechargeMoney().multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString();
                if (rechargeMoney.equals(payAmount)) {
                    //充值金额相等 判断充值结果是否  "10".equels
                    if ("10".equals(payResult)) {
                        //充值结果一致  向用户资金表添加充值金额
                        num = accountMapper.updateUserAvaliableMoney(recharge.getUid(), recharge.getRechargeMoney());
                        if (num < 1) {
                            throw new RuntimeException("充值 更新用户余额失败");
                        }
                        //修改充值状态为1 充值成功
                        num = rechargeMapper.updateRechargeStatus(orderId, YlbConstant.USER_RECHARGE_TRUE);
                        if (num < 1) {
                            throw new RuntimeException("充值 修改充值记录的充值状态为充值成功 失败");
                        }
                        result = Result.ok();
                        result.setCodeEnum(CodeEnum.RC_RECHARGE_SUCCEED);
                    } else {
                        //充值结果不一致
                        result.setCodeEnum(CodeEnum.RC_RECHARGE_ORDER_PAYRESULT_NOEQUALITY);
                        //修改充值记录状态 为2  充值失败
                        num = rechargeMapper.updateRechargeStatus(orderId, YlbConstant.USER_RECHARGE_FALSE);
                        if (num < 1) {
                            throw new RuntimeException("充值 修改充值记录的充值状态为充值失败 失败");
                        }
                        result = Result.ok();
                        result.setCodeEnum(CodeEnum.RC_RECHARGE_NOTHING);
                    }
                } else {
                    //充值金额不相等
                    result.setCodeEnum(CodeEnum.RC_RECHARGE_ORDER_MONEY_NOEQUALITY);
                }
            } else {
                //订单已经处理完成
                result.setCodeEnum(CodeEnum.RC_RECHARGE_ORDER_SUCCESS);
            }

        } else {
            //订单不存在
            result.setCodeEnum(CodeEnum.RC_RECHARGE_ORDER_NO_EXIST);
        }
        return result;
    }

    //验签失败 修改充值状态为3  验签失败
    @Override
    public boolean editRechargeStatus(String orderId, int signatureVerificationFalse) {
        int num = rechargeMapper.updateRechargeStatus(orderId,signatureVerificationFalse);
        return num > 0;
    }

    //查询是否有异步通知处理的结果
    @Override
    public Recharge queryOrderStatus(String orderId) {
        Recharge recharge = rechargeMapper.selectRechargeStatus(orderId);
        return recharge;
    }
}
