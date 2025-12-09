package com.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KeyIndexVO {
    private BigDecimal totalIncomeMonth; // 当月总收入
    private BigDecimal totalExpenseMonth; // 当月总支出
    private BigDecimal monthBalance; // 当月结余
    private BigDecimal totalAsset; // 总资产 (所有账户余额之和)
    private BigDecimal totalDebt; // 总负债 (如果有负债账户类型)
    private BigDecimal netAsset; // 净资产
}