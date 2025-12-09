package com.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    private Long id; // 用于更新时
    @NotBlank(message = "账户名称不能为空")
    private String accountName;
    @NotBlank(message = "账户类型不能为空")
    private String accountType;
    @NotNull(message = "初始余额不能为空")
    @DecimalMin(value = "0.00", message = "初始余额不能小于0")
    private BigDecimal initialBalance;
    private String remark;
}