package com.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "收支记录传输对象")
public class FinanceRecordDTO {

    @Schema(description = "记录ID (修改时必填)")
    private Long id;

    @Schema(description = "类型：1-收入，2-支出", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "账户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long accountId;

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate recordDate;

    @Schema(description = "备注")
    private String remark;
}