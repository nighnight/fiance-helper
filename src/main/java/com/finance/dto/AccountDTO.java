package com.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Schema(description = "账户传输对象")
public class AccountDTO {

    @Schema(description = "账户ID (修改时必传)")
    private Long id;

    @Schema(description = "账户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountName;

    @Schema(description = "账户类型 (微信/支付宝/银行卡/现金)")
    private String accountType;

    @Schema(description = "初始余额")
    private BigDecimal initialBalance;

    @Schema(description = "备注")
    private String remark;
}