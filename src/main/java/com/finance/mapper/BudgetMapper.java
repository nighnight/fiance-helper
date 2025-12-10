package com.finance.mapper;

import com.finance.po.Budget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BudgetMapper {
    // 查询某用户某月的预算列表
    List<Budget> selectList(@Param("userId") Long userId, @Param("cycleValue") String cycleValue);

    Budget selectById(Long id);

    // 检查是否存在重复预算
    Budget selectUnique(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("cycleValue") String cycleValue);

    void insert(Budget budget);
    void update(Budget budget);
    void deleteById(Long id);
}