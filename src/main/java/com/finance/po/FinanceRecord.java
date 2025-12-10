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
    private Integer type;
    private Long categoryId;
    private Long accountId;
    private LocalDate recordDate;
    private String remark;
    private String voucherUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // ▼▼▼▼▼▼▼▼ 必须补上这两个字段（非数据库表字段，用于展示） ▼▼▼▼▼▼▼▼
    private String categoryName;
    private String accountName;
    // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
}