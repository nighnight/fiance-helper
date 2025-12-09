package com.finance.service.impl;

import com.finance.dto.AccountDTO;
import com.finance.exception.BusinessException;
import com.finance.mapper.AccountMapper;
import com.finance.po.Account;
import com.finance.service.AccountService;
import com.finance.vo.AccountVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    @Transactional
    public void addAccount(Long userId, AccountDTO accountDTO) {
        Account account = new Account();
        BeanUtils.copyProperties(accountDTO, account);
        account.setUserId(userId);
        account.setCurrentBalance(accountDTO.getInitialBalance()); // 初始余额即当前余额
        account.setIsEnabled(1); // 默认启用
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateTime(LocalDateTime.now());

        int result = accountMapper.insert(account);
        if (result != 1) {
            throw new BusinessException("添加账户失败");
        }
    }

    @Override
    @Transactional
    public void updateAccount(Long userId, AccountDTO accountDTO) {
        if (accountDTO.getId() == null) {
            throw new BusinessException("账户ID不能为空");
        }
        Account existingAccount = accountMapper.selectByPrimaryKey(accountDTO.getId(), userId);
        if (existingAccount == null) {
            throw new BusinessException("账户不存在或无权修改");
        }

        // 如果初始余额发生变化，需要调整当前余额
        if (!existingAccount.getInitialBalance().equals(accountDTO.getInitialBalance())) {
            BigDecimal balanceChange = accountDTO.getInitialBalance().subtract(existingAccount.getInitialBalance());
            existingAccount.setCurrentBalance(existingAccount.getCurrentBalance().add(balanceChange));
            existingAccount.setInitialBalance(accountDTO.getInitialBalance());
        }

        existingAccount.setAccountName(accountDTO.getAccountName());
        existingAccount.setAccountType(accountDTO.getAccountType());
        existingAccount.setRemark(accountDTO.getRemark());
        existingAccount.setUpdateTime(LocalDateTime.now());

        int result = accountMapper.updateByPrimaryKeySelective(existingAccount);
        if (result != 1) {
            throw new BusinessException("更新账户失败");
        }
    }

    @Override
    @Transactional
    public void deleteAccount(Long userId, Long accountId) {
        Account existingAccount = accountMapper.selectByPrimaryKey(accountId, userId);
        if (existingAccount == null) {
            throw new BusinessException("账户不存在或无权删除");
        }
        // TODO: 删除账户时，需要考虑该账户下的所有收支记录如何处理 (软删除/删除前先清空记录等)
        // 这里只是简单删除账户，实际应用中可能需要更复杂的逻辑
        int result = accountMapper.deleteByPrimaryKey(accountId, userId);
        if (result != 1) {
            throw new BusinessException("删除账户失败");
        }
    }

    @Override
    public AccountVO getAccountById(Long userId, Long accountId) {
        Account account = accountMapper.selectByPrimaryKey(accountId, userId);
        if (account == null) {
            return null;
        }
        return convertToVO(account);
    }

    @Override
    public List<AccountVO> getAccountsByUserId(Long userId) {
        List<Account> accounts = accountMapper.selectByUserId(userId);
        return accounts.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateAccountBalance(Long accountId, BigDecimal amount) {
        int result = accountMapper.updateAccountBalance(accountId, amount);
        if (result != 1) {
            throw new BusinessException("更新账户余额失败");
        }
    }

    @Override
    public BigDecimal getTotalAssets(Long userId) {
        BigDecimal totalBalance = accountMapper.sumCurrentBalanceByUserId(userId);
        return totalBalance != null ? totalBalance : BigDecimal.ZERO;
    }

    private AccountVO convertToVO(Account account) {
        AccountVO vo = new AccountVO();
        BeanUtils.copyProperties(account, vo);
        return vo;
    }
}