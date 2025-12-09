package com.finance.mapper;

import com.finance.po.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysUserMapper {
    int insert(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    SysUser selectByUsername(@Param("username") String username);

    int updateByPrimaryKeySelective(SysUser record);
}