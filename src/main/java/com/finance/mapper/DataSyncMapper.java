package com.finance.mapper;

import com.finance.po.DataSync;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DataSyncMapper {
    int insert(DataSync record);

    DataSync selectByUserIdAndSyncType(@Param("userId") Long userId, @Param("syncType") String syncType);

    int updateByPrimaryKeySelective(DataSync record);

    int updateMaxSyncId(@Param("userId") Long userId, @Param("syncType") String syncType, @Param("maxSyncId") Long maxSyncId);
}