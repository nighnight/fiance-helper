package com.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
public class TrendVO {
    private String month; // 月份，如 "2023-01"
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance; // 余额 (收入 - 支出)
}