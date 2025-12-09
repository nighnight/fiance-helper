package com.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountVO {
    private Long id;
    private String accountName;
    private String accountType;
    private BigDecimal currentBalance;
    private String remark;
    private LocalDateTime updateTime;
}