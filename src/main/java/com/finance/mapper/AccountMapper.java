package com.finance.mapper;

import com.finance.po.Account;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AccountMapper {
    // 查询某用户下的所有账户
    List<Account> selectByUserId(Long userId);

    // 插入
    void insert(Account account);

    // 根据ID查询
    Account selectById(Long id);

    // 更新
    void update(Account account);

    // 删除
    void deleteById(Long id);
}