package com.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinanceRecordDTO implements Serializable {
    private Long id; // 用于更新时
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
    @NotNull(message = "记录类型不能为空")
    private Integer type; // 1-收入，2-支出
    @NotNull(message = "类别不能为空")
    private Long categoryId;
    @NotNull(message = "账户不能为空")
    private Long accountId;
    @NotNull(message = "记录日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordDate;
    private String remark;
    private String voucherUrl;
}