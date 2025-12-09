package com.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetDTO {
    private Long id; // 用于更新时
    @NotNull(message = "类别不能为空")
    private Long categoryId; // 0-总预算
    @NotNull(message = "预算周期类型不能为空")
    @Min(value = 1, message = "预算周期类型无效")
    @Max(value = 3, message = "预算周期类型无效")
    private Integer cycleType; // 1-月度，2-季度，3-年度
    @NotBlank(message = "预算周期值不能为空")
    private String cycleValue; // 如：2024-10, 2024-Q3
    @NotNull(message = "预算金额不能为空")
    @DecimalMin(value = "0.01", message = "预算金额必须大于0")
    private BigDecimal budgetAmount;
}