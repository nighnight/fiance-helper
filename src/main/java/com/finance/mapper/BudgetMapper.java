package com.finance.mapper;

import com.finance.po.Budget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetMapper {
    int insert(Budget record);

    Budget selectByPrimaryKey(@Param("id") Long id, @Param("userId") Long userId);

    Budget selectByUniqueKey(@Param("userId") Long userId,
                             @Param("categoryId") Long categoryId,
                             @Param("cycleType") Integer cycleType,
                             @Param("cycleValue") String cycleValue);

    List<Budget> selectByUserId(@Param("userId") Long userId);

    List<Budget> selectByUserIdAndCycle(@Param("userId") Long userId,
                                        @Param("cycleType") Integer cycleType,
                                        @Param("cycleValue") String cycleValue);

    int updateByPrimaryKeySelective(Budget record);

    int deleteByPrimaryKey(@Param("id") Long id, @Param("userId") Long userId);

    // 更新预算已使用金额
    int updateUsedAmount(@Param("id") Long id, @Param("usedAmountDelta") BigDecimal usedAmountDelta);
}