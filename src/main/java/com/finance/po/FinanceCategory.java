package com.finance.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FinanceCategory {
    private Long id;
    private Long userId; // 0-系统默认类别，用户自定义为自己的user_id
    private String categoryName;
    private Integer type; // 1-收入，2-支出
    private Integer sort;
    private Integer isDefault; // 1-是，0-用户自定义
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}