package com.finance.service.impl;

import com.finance.mapper.DataSyncMapper;
import com.finance.po.DataSync;
import com.finance.service.SyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SyncServiceImpl implements SyncService {

    @Autowired
    private DataSyncMapper dataSyncMapper;

    @Override
    public DataSync getSyncInfo(Long userId, String syncType) {
        return dataSyncMapper.selectByUserIdAndSyncType(userId, syncType);
    }

    @Override
    @Transactional
    public void updateSyncInfo(Long userId, String syncType, Long maxSyncId) {
        DataSync existingSyncInfo = dataSyncMapper.selectByUserIdAndSyncType(userId, syncType);
        if (existingSyncInfo == null) {
            // 第一次同步，插入记录
            DataSync newSyncInfo = new DataSync();
            newSyncInfo.setUserId(userId);
            newSyncInfo.setSyncType(syncType);
            newSyncInfo.setMaxSyncId(maxSyncId);
            newSyncInfo.setLastSyncTime(LocalDateTime.now());
            dataSyncMapper.insert(newSyncInfo);
        } else {
            // 更新已有的同步记录
            existingSyncInfo.setMaxSyncId(maxSyncId);
            existingSyncInfo.setLastSyncTime(LocalDateTime.now());
            dataSyncMapper.updateByPrimaryKeySelective(existingSyncInfo);
        }
    }
}