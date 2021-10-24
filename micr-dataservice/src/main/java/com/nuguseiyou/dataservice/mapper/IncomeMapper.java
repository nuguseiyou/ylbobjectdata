package com.nuguseiyou.dataservice.mapper;

import com.nuguseiyou.model.Income;
import com.nuguseiyou.model.ext.UserIncome;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IncomeMapper {
    /**
     * 查询用户最近收益记录
     *
     * @param uid      用户id
     * @param offset   页码
     * @param rows 页面数据大小
     * @return 返回用户最近收益记录集合
     */
    List<UserIncome> selectUserIncomeRecord(@Param("uid") Integer uid, @Param("offset") Integer offset, @Param("rows") Integer rows);

    /**
     * 查询用户最近收益记录数
     *
     * @param uid 用户id
     * @return 返回用户最近收益记录数
     */
    int selectUserIncomeRecordCount(@Param("uid") Integer uid);


    /**
     * @param income 收益记录
     * @return
     */
    int insertIncomeRecord(Income income);

    //查询昨天到期的收益记录
    List<Income> selectExpireIncomeRecord(@Param("investIncomeNotReturn") int investIncomeNotReturn);

    //修改收益记录的收益状态
    int updateIncomeStatus(@Param("id") Integer id, @Param("investIncomeReturn") int investIncomeReturn);
}