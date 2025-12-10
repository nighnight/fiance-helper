package com.finance.mapper;

import com.finance.po.FinanceRecord;
import com.finance.vo.ChartVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FinanceRecordMapper {
    // 查询列表（关联分类表和账户表，显示名称）
    List<FinanceRecord> selectList(@Param("userId") Long userId);

    // 查询单条
    FinanceRecord selectById(Long id);

    // 插入
    void insert(FinanceRecord record);

    // 更新
    void update(FinanceRecord record);

    // 删除
    void deleteById(Long id);

    // 按月统计（给首页用的）
    List<FinanceRecord> selectByMonth(@Param("userId") Long userId, @Param("month") String month);

    // ... 原有方法 ...

    // 统计某月各分类的支出 (饼图)
    List<ChartVO> selectCategoryExpenseStats(@Param("userId") Long userId, @Param("month") String month);

    // 统计某月每日收支 (折线图) - type: 1收入 2支出
    List<ChartVO> selectDailyStats(@Param("userId") Long userId, @Param("month") String month, @Param("type") Integer type);
}