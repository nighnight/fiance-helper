package com.finance.service.impl;

import com.finance.mapper.AccountMapper;
import com.finance.mapper.FinanceCategoryMapper;
import com.finance.mapper.FinanceRecordMapper;
import com.finance.po.FinanceCategory;
import com.finance.service.ChartService;
import com.finance.util.DateUtil;
import com.finance.vo.KeyIndexVO;
import com.finance.vo.PieVO;
import com.finance.vo.TrendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartServiceImpl implements ChartService {

    @Autowired
    private FinanceRecordMapper financeRecordMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private FinanceCategoryMapper financeCategoryMapper;

    @Override
    public List<TrendVO> getMonthlyTrend(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rawData = financeRecordMapper.getMonthlyTrendByUserId(userId, startDate, endDate);
        List<TrendVO> trendList = new ArrayList<>();

        Map<YearMonth, Map<Integer, BigDecimal>> groupedData = rawData.stream()
                .collect(Collectors.groupingBy(
                        map -> (YearMonth) map.get("month"), // 注意：MyBatis XML中映射为YearMonth对象
                        Collectors.groupingBy(
                                map -> (Integer) map.get("type"), // 1-收入, 2-支出
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        map -> (BigDecimal) map.get("total_amount"),
                                        BigDecimal::add
                                )
                        )
                ));

        List<YearMonth> months = DateUtil.getYearMonthsBetween(startDate, endDate);

        for (YearMonth ym : months) {
            TrendVO trendVO = new TrendVO();
            trendVO.setMonth(ym.toString()); // 例如 "2023-01"
            trendVO.setIncome(BigDecimal.ZERO);
            trendVO.setExpense(BigDecimal.ZERO);

            Map<Integer, BigDecimal> monthData = groupedData.get(ym);
            if (monthData != null) {
                trendVO.setIncome(monthData.getOrDefault(1, BigDecimal.ZERO));
                trendVO.setExpense(monthData.getOrDefault(2, BigDecimal.ZERO));
            }
            trendVO.setBalance(trendVO.getIncome().subtract(trendVO.getExpense()));
            trendList.add(trendVO);
        }
        return trendList.stream()
                .sorted(Comparator.comparing(TrendVO::getMonth))
                .collect(Collectors.toList());
    }

    @Override
    public List<PieVO> getExpenseCategoryPieData(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> summaryData = financeRecordMapper.getExpenseCategorySummaryByUserId(userId, startDate, endDate);
        return processPieData(summaryData, 2); // 2 represents expenses
    }

    @Override
    public List<PieVO> getIncomeCategoryPieData(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> summaryData = financeRecordMapper.getIncomeCategorySummaryByUserId(userId, startDate, endDate);
        return processPieData(summaryData, 1); // 1 represents income
    }

    private List<PieVO> processPieData(List<Map<String, Object>> summaryData, Integer type) {
        BigDecimal totalAmount = summaryData.stream()
                .map(map -> (BigDecimal) map.get("total_amount"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PieVO> pieData = new ArrayList<>();
        for (Map<String, Object> map : summaryData) {
            Long categoryId = (Long) map.get("category_id");
            BigDecimal amount = (BigDecimal) map.get("total_amount");

            FinanceCategory category = financeCategoryMapper.selectByPrimaryKey(categoryId);
            if (category == null) { // 类别可能被删除或系统类别不存在
                // 尝试根据type和用户ID来获取默认名称或标记为“未知类别”
                category = new FinanceCategory(); // 创建一个临时类别对象
                category.setCategoryName("未知类别(" + categoryId + ")");
            }

            PieVO pieVO = new PieVO();
            pieVO.setName(category.getCategoryName());
            pieVO.setValue(amount);
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal percentage = amount.divide(totalAmount, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                pieVO.setPercentage(percentage.setScale(2, RoundingMode.HALF_UP) + "%");
            } else {
                pieVO.setPercentage("0.00%");
            }
            pieData.add(pieVO);
        }
        return pieData.stream()
                .sorted(Comparator.comparing(PieVO::getValue).reversed()) // 按金额降序
                .collect(Collectors.toList());
    }

    @Override
    public KeyIndexVO getKeyFinancialIndex(Long userId, LocalDate date) {
        KeyIndexVO keyIndexVO = new KeyIndexVO();
        YearMonth currentYearMonth = YearMonth.from(date);

        // 当月总收入
        BigDecimal totalIncomeMonth = financeRecordMapper.sumAmountByUserIdAndTypeAndMonth(userId, 1, currentYearMonth);
        keyIndexVO.setTotalIncomeMonth(Optional.ofNullable(totalIncomeMonth).orElse(BigDecimal.ZERO));

        // 当月总支出
        BigDecimal totalExpenseMonth = financeRecordMapper.sumAmountByUserIdAndTypeAndMonth(userId, 2, currentYearMonth);
        keyIndexVO.setTotalExpenseMonth(Optional.ofNullable(totalExpenseMonth).orElse(BigDecimal.ZERO));

        // 当月结余
        keyIndexVO.setMonthBalance(keyIndexVO.getTotalIncomeMonth().subtract(keyIndexVO.getTotalExpenseMonth()));

        // 总资产 (所有账户余额之和)
        BigDecimal totalAsset = accountMapper.sumCurrentBalanceByUserId(userId);
        keyIndexVO.setTotalAsset(Optional.ofNullable(totalAsset).orElse(BigDecimal.ZERO));

        // TODO: 如果有负债账户类型，这里计算总负债
        keyIndexVO.setTotalDebt(BigDecimal.ZERO); // 暂不实现负债统计

        // 净资产
        keyIndexVO.setNetAsset(keyIndexVO.getTotalAsset().subtract(keyIndexVO.getTotalDebt()));

        return keyIndexVO;
    }
}