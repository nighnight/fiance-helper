package com.finance.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Budget {
    private Long id;
    private Long userId;
    private Long categoryId; // 0-总预算
    private Integer cycleType; // 1-月度，2-季度，3-年度
    private String cycleValue; // 如：2024-10, 2024-Q3
    private BigDecimal budgetAmount;
    private BigDecimal usedAmount; // 已使用金额
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}