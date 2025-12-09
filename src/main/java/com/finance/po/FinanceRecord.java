package com.finance.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FinanceRecord {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private Integer type; // 1-收入，2-支出
    private Long categoryId;
    private Long accountId;
    private LocalDate recordDate;
    private String remark;
    private String voucherUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}