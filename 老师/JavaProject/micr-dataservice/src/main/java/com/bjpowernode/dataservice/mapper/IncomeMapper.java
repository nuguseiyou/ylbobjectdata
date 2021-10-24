package com.bjpowernode.dataservice.mapper;

import com.bjpowernode.entity.Income;
import com.bjpowernode.entity.ext.UserIncome;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IncomeMapper {

    /**
     * @param uid      用户id
     * @param offSet   起始位置
     * @param pageSize 大小
     * @return 用户收益记录集合
     */
    List<UserIncome> selectIncomeListUid(@Param("uid") Integer uid,
                                         @Param("offSet") int offSet,
                                         @Param("rows") Integer pageSize);

    //用户记录总数
    int selectIncomeCountUid(@Param("uid") Integer uid);

    //创建收益记录
    int insert(Income income);

    //要执行收益返还的记录
    List<Income> selectExipreIncome();

    //更新收益记录状态为 1
    int updateStatusBack(@Param("id") Integer id);
}