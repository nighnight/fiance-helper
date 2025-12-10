package com.finance.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ChartVO {
    private String name;  // 分类名 或 日期
    private BigDecimal value; // 金额
}