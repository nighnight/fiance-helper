package com.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RecordVO {
    private Long id;
    private BigDecimal amount;
    private Integer type; // 1-收入，2-支出
    private String typeName;
    private Long categoryId;
    private String categoryName;
    private Long accountId;
    private String accountName;
    private LocalDate recordDate;
    private String remark;
    private String voucherUrl;
    private LocalDateTime createTime;
}