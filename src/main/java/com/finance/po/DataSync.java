package com.finance.po;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DataSync {
    private Long id;
    private Long userId;
    private String syncType; // record-收支记录，account-账户，category-类别
    private Long maxSyncId; // 已同步的最大ID
    private LocalDateTime lastSyncTime;
}