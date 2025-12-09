package com.finance.service;

import com.finance.po.DataSync;

import java.time.LocalDateTime;

public interface SyncService {
    /**
     * 获取用户某种数据类型的最后同步信息
     * @param userId 用户ID
     * @param syncType 同步数据类型 (e.g., "record", "account", "category")
     * @return DataSync 对象
     */
    DataSync getSyncInfo(Long userId, String syncType);

    /**
     * 更新或插入同步信息
     * @param userId 用户ID
     * @param syncType 同步数据类型
     * @param maxSyncId 当前已同步的最大ID
     */
    void updateSyncInfo(Long userId, String syncType, Long maxSyncId);

    // 对于离线同步，可能还需要一个拉取增量数据的接口
    // List<RecordVO> getDeltaRecords(Long userId, Long lastSyncRecordId);
    // List<AccountVO> getDeltaAccounts(Long userId, Long lastSyncAccountId); etc.
}