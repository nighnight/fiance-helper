package com.finance.service;

import com.finance.dto.AccountDTO;
import com.finance.po.Account;
import com.finance.vo.AccountVO;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    void addAccount(Long userId, AccountDTO accountDTO);
    void updateAccount(Long userId, AccountDTO accountDTO);
    void deleteAccount(Long userId, Long accountId);
    AccountVO getAccountById(Long userId, Long accountId);
    List<AccountVO> getAccountsByUserId(Long userId);
    void updateAccountBalance(Long accountId, BigDecimal amount); // 用于收支记录增删改
    BigDecimal getTotalAssets(Long userId);
}