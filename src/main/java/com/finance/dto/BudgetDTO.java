package com.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "预算传输对象")
public class BudgetDTO {
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "分类ID (0代表总预算)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "周期类型: 1-月度 (目前只支持月度)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer cycleType = 1;

    @Schema(description = "周期值，例如 '2023-12'", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cycleValue;

    @Schema(description = "预算金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal budgetAmount;
}