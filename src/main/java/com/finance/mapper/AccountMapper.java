package com.finance.mapper;

import com.finance.po.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountMapper {
    int insert(Account record);

    Account selectByPrimaryKey(@Param("id") Long id, @Param("userId") Long userId);

    List<Account> selectByUserId(@Param("userId") Long userId);

    int updateByPrimaryKeySelective(Account record);

    int deleteByPrimaryKey(@Param("id") Long id, @Param("userId") Long userId);

    // 更新账户余额
    int updateAccountBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

    // 获取所有账户总余额
    BigDecimal sumCurrentBalanceByUserId(@Param("userId") Long userId);
}