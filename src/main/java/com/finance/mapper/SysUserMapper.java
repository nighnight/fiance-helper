package com.finance.mapper;

import com.finance.po.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {
    // 根据用户名查询
    SysUser selectByUsername(@Param("username") String username);

    // 插入新用户
    int insert(SysUser sysUser);

    void update(SysUser user);

    SysUser selectById(Long id);
}