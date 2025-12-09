package com.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PieVO {
    private String name; // 类别名称
    private BigDecimal value; // 金额
    private String percentage; // 占比 (例如 "25.3%")
}