package com.finance.mapper;

import com.finance.po.FinanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Mapper
public interface FinanceRecordMapper {
    int insert(FinanceRecord record);

    FinanceRecord selectByPrimaryKey(@Param("id") Long id, @Param("userId") Long userId);

    List<FinanceRecord> selectByUserId(@Param("userId") Long userId);

    List<FinanceRecord> selectByUserIdAndDateRange(@Param("userId") Long userId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   @Param("type") Integer type,
                                                   @Param("categoryId") Long categoryId,
                                                   @Param("accountId") Long accountId);

    int updateByPrimaryKeySelective(FinanceRecord record);

    int deleteByPrimaryKey(@Param("id") Long id, @Param("userId") Long userId);

    // 统计指定月份的收入、支出
    BigDecimal sumAmountByUserIdAndTypeAndMonth(@Param("userId") Long userId,
                                                @Param("type") Integer type,
                                                @Param("yearMonth") YearMonth yearMonth);

    // 统计指定时间段内，按月份的收入支出趋势
    List<Map<String, Object>> getMonthlyTrendByUserId(@Param("userId") Long userId,
                                                      @Param("startDate") LocalDate startDate,
                                                      @Param("endDate") LocalDate endDate);

    // 统计指定时间段内，按支出类别汇总
    List<Map<String, Object>> getExpenseCategorySummaryByUserId(@Param("userId") Long userId,
                                                                @Param("startDate") LocalDate startDate,
                                                                @Param("endDate") LocalDate endDate);

    // 统计指定时间段内，按收入类别汇总
    List<Map<String, Object>> getIncomeCategorySummaryByUserId(@Param("userId") Long userId,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);

    // 获取某用户最新的记录ID
    Long selectMaxIdByUserId(@Param("userId") Long userId);
}