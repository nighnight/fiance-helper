package com.finance.po;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Account {
    private Long id;
    private Long userId;
    private String accountName;
    private String accountType; // 账户类型（银行卡、支付宝、微信、现金）
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private Integer isEnabled; // 1-是，0-否
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}