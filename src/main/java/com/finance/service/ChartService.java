package com.finance.service;

import com.finance.vo.KeyIndexVO;
import com.finance.vo.PieVO;
import com.finance.vo.TrendVO;

import java.time.LocalDate;
import java.util.List;

public interface ChartService {
    /**
     * 获取月度收支趋势数据
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 月度趋势列表
     */
    List<TrendVO> getMonthlyTrend(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定时间范围内支出类别饼图数据
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 支出类别饼图数据列表
     */
    List<PieVO> getExpenseCategoryPieData(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取指定时间范围内收入类别饼图数据
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收入类别饼图数据列表
     */
    List<PieVO> getIncomeCategoryPieData(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户一段时间内的关键财务指标
     * @param userId 用户ID
     * @param yearMonth 当年月，用于计算当月收支
     * @return 关键指标VO
     */
    KeyIndexVO getKeyFinancialIndex(Long userId, LocalDate yearMonth);
}