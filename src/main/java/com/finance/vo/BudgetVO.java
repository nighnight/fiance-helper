package com.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetVO {
    private Long id;
    private Long categoryId;
    private String categoryName; // 如果是总预算，这里可以显示“总预算”
    private Integer cycleType; // 1-月度，2-季度，3-年度
    private String cycleValue;
    private String cycleTypeName; // 月度/季度/年度
    private BigDecimal budgetAmount;
    private BigDecimal usedAmount;
    private BigDecimal remainingAmount; // 剩余金额
    private BigDecimal progress; // 使用进度百分比
}