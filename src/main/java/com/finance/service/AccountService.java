package com.finance.service;
import com.finance.dto.AccountDTO;
import com.finance.po.Account;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public interface AccountService {
    List<Account> getList(HttpSession session);
    void addAccount(AccountDTO accountDTO, HttpSession session);
    Account getById(Long id);
    void updateAccount(AccountDTO accountDTO);
    void deleteAccount(Long id);
}