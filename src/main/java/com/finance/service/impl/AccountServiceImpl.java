package com.finance.service.impl;

import com.finance.dto.AccountDTO;
import com.finance.mapper.AccountMapper;
import com.finance.po.Account;
import com.finance.po.SysUser;
import com.finance.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public List<Account> getList(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        return accountMapper.selectByUserId(user.getId());
    }

    @Override
    public void addAccount(AccountDTO accountDTO, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        Account account = new Account();
        BeanUtils.copyProperties(accountDTO, account); // 属性拷贝

        account.setUserId(user.getId());
        // 初始余额即为当前余额
        if(account.getInitialBalance() == null) {
            account.setInitialBalance(BigDecimal.ZERO);
        }
        account.setCurrentBalance(account.getInitialBalance());
        account.setIsEnabled(1); // 默认启用

        accountMapper.insert(account);
    }

    @Override
    public Account getById(Long id) {
        return accountMapper.selectById(id);
    }

    @Override
    public void updateAccount(AccountDTO accountDTO) {
        Account account = new Account();
        BeanUtils.copyProperties(accountDTO, account);
        accountMapper.update(account);
    }

    @Override
    public void deleteAccount(Long id) {
        accountMapper.deleteById(id);
    }
}