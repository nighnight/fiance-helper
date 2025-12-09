package com.finance.service.impl;

import com.finance.dto.FinanceRecordDTO;
import com.finance.exception.BusinessException;
import com.finance.mapper.AccountMapper;
import com.finance.mapper.FinanceCategoryMapper;
import com.finance.mapper.FinanceRecordMapper;
import com.finance.po.Account;
import com.finance.po.FinanceCategory;
import com.finance.po.FinanceRecord;
import com.finance.service.AccountService; // 注意这里引用了AccountService
import com.finance.service.BudgetService; // 引用BudgetService
import com.finance.service.FinanceRecordService;
import com.finance.util.DateUtil;
import com.finance.vo.RecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinanceRecordServiceImpl implements FinanceRecordService {

    @Autowired
    private FinanceRecordMapper financeRecordMapper;
    @Autowired
    private AccountMapper accountMapper; // 直接操作AccountMapper以控制事务
    @Autowired
    private FinanceCategoryMapper financeCategoryMapper; // 直接操作类别Mapper
    @Autowired
    private BudgetService budgetService; // 注入预算服务

    @Override
    @Transactional
    public void addRecord(Long userId, FinanceRecordDTO recordDTO) {
        // 1. 校验账户和类别是否存在且属于该用户
        Account account = accountMapper.selectByPrimaryKey(recordDTO.getAccountId(), userId);
        if (account == null) {
            throw new BusinessException("账户不存在或不属于您");
        }
        FinanceCategory category = financeCategoryMapper.selectByPrimaryKey(recordDTO.getCategoryId());
        // 系统默认类别user_id为0，用户自定义类别user_id为用户ID
        if (category == null || (!category.getUserId().equals(0L) && !category.getUserId().equals(userId))) {
            throw new BusinessException("类别不存在或不属于您");
        }
        if (!category.getType().equals(recordDTO.getType())) {
            throw new BusinessException("记录类型与所选类别类型不匹配");
        }

        // 2. 更新账户余额
        BigDecimal amount = recordDTO.getAmount();
        if (recordDTO.getType() == 1) { // 收入
            account.setCurrentBalance(account.getCurrentBalance().add(amount));
        } else { // 支出
            account.setCurrentBalance(account.getCurrentBalance().subtract(amount));
        }
        int updateAccountResult = accountMapper.updateByPrimaryKeySelective(account);
        if (updateAccountResult != 1) {
            throw new BusinessException("更新账户余额失败");
        }

        // 3. 插入收支记录
        FinanceRecord record = new FinanceRecord();
        BeanUtils.copyProperties(recordDTO, record);
        record.setUserId(userId);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        int insertResult = financeRecordMapper.insert(record);
        if (insertResult != 1) {
            throw new BusinessException("添加收支记录失败");
        }

        // 4. 更新相关预算的已使用金额 (支出才影响预算)
        if (recordDTO.getType() == 2) {
            // 月度预算
            String monthCycleValue = DateUtil.formatYearMonth(recordDTO.getRecordDate());
            budgetService.updateBudgetUsedAmount(userId, recordDTO.getCategoryId(), 1, monthCycleValue, amount);
            // 总月度预算
            budgetService.updateBudgetUsedAmount(userId, 0L, 1, monthCycleValue, amount);

            // TODO: 如果有季度或年度预算，也要在这里更新
        }
    }

    @Override
    @Transactional
    public void updateRecord(Long userId, FinanceRecordDTO recordDTO) {
        if (recordDTO.getId() == null) {
            throw new BusinessException("记录ID不能为空");
        }
        FinanceRecord existingRecord = financeRecordMapper.selectByPrimaryKey(recordDTO.getId(), userId);
        if (existingRecord == null) {
            throw new BusinessException("收支记录不存在或无权修改");
        }

        // 1. 检查账户和类别
        Account oldAccount = accountMapper.selectByPrimaryKey(existingRecord.getAccountId(), userId);
        Account newAccount = accountMapper.selectByPrimaryKey(recordDTO.getAccountId(), userId);
        if (oldAccount == null || newAccount == null) {
            throw new BusinessException("账户不存在或不属于您");
        }
        FinanceCategory oldCategory = financeCategoryMapper.selectByPrimaryKey(existingRecord.getCategoryId());
        FinanceCategory newCategory = financeCategoryMapper.selectByPrimaryKey(recordDTO.getCategoryId());
        if (oldCategory == null || newCategory == null || (!newCategory.getUserId().equals(0L) && !newCategory.getUserId().equals(userId))) {
            throw new BusinessException("类别不存在或不属于您");
        }
        if (!newCategory.getType().equals(recordDTO.getType())) {
            throw new BusinessException("记录类型与所选类别类型不匹配");
        }

        // 2. 回退旧账户余额和旧预算
        BigDecimal oldAmount = existingRecord.getAmount();
        BigDecimal newAmount = recordDTO.getAmount();

        if (existingRecord.getType() == 1) { // 旧记录是收入
            oldAccount.setCurrentBalance(oldAccount.getCurrentBalance().subtract(oldAmount));
        } else { // 旧记录是支出
            oldAccount.setCurrentBalance(oldAccount.getCurrentBalance().add(oldAmount));
            // 回退旧预算
            String oldMonthCycleValue = DateUtil.formatYearMonth(existingRecord.getRecordDate());
            budgetService.updateBudgetUsedAmount(userId, existingRecord.getCategoryId(), 1, oldMonthCycleValue, oldAmount.negate());
            budgetService.updateBudgetUsedAmount(userId, 0L, 1, oldMonthCycleValue, oldAmount.negate());
        }
        accountMapper.updateByPrimaryKeySelective(oldAccount);

        // 3. 应用新账户余额和新预算
        if (recordDTO.getType() == 1) { // 新记录是收入
            newAccount.setCurrentBalance(newAccount.getCurrentBalance().add(newAmount));
        } else { // 新记录是支出
            newAccount.setCurrentBalance(newAccount.getCurrentBalance().subtract(newAmount));
            // 更新新预算
            String newMonthCycleValue = DateUtil.formatYearMonth(existingRecord.getRecordDate());
            budgetService.updateBudgetUsedAmount(userId, recordDTO.getCategoryId(), 1, newMonthCycleValue, newAmount);
            budgetService.updateBudgetUsedAmount(userId, 0L, 1, newMonthCycleValue, newAmount);
        }
        if (!oldAccount.getId().equals(newAccount.getId())) { // 如果账户切换了，需要更新新账户
            accountMapper.updateByPrimaryKeySelective(newAccount);
        } else { // 如果账户没切换，已在前面回退 oldAccount 并更新
            accountMapper.updateByPrimaryKeySelective(newAccount);
        }

        // 4. 更新收支记录
        BeanUtils.copyProperties(recordDTO, existingRecord); // 将新数据DTO覆盖到PO
        existingRecord.setUserId(userId); // 确保用户ID不变
        existingRecord.setUpdateTime(LocalDateTime.now());
        int updateResult = financeRecordMapper.updateByPrimaryKeySelective(existingRecord);
        if (updateResult != 1) {
            throw new BusinessException("更新收支记录失败");
        }
    }

    @Override
    @Transactional
    public void deleteRecord(Long userId, Long recordId) {
        FinanceRecord existingRecord = financeRecordMapper.selectByPrimaryKey(recordId, userId);
        if (existingRecord == null) {
            throw new BusinessException("收支记录不存在或无权删除");
        }

        // 1. 回退账户余额
        Account account = accountMapper.selectByPrimaryKey(existingRecord.getAccountId(), userId);
        if (account == null) { // 理论上不应该发生，除非账户被删除了
            throw new BusinessException("关联账户不存在");
        }
        if (existingRecord.getType() == 1) { // 收入
            account.setCurrentBalance(account.getCurrentBalance().subtract(existingRecord.getAmount()));
        } else { // 支出
            account.setCurrentBalance(account.getCurrentBalance().add(existingRecord.getAmount()));
            // 回退预算
            String monthCycleValue = DateUtil.formatYearMonth(existingRecord.getRecordDate());
            budgetService.updateBudgetUsedAmount(userId, existingRecord.getCategoryId(), 1, monthCycleValue, existingRecord.getAmount().negate());
            budgetService.updateBudgetUsedAmount(userId, 0L, 1, monthCycleValue, existingRecord.getAmount().negate());
        }
        int updateAccountResult = accountMapper.updateByPrimaryKeySelective(account);
        if (updateAccountResult != 1) {
            throw new BusinessException("回退账户余额失败");
        }

        // 2. 删除记录
        int deleteResult = financeRecordMapper.deleteByPrimaryKey(recordId, userId);
        if (deleteResult != 1) {
            throw new BusinessException("删除收支记录失败");
        }
    }

    @Override
    public RecordVO getRecordById(Long userId, Long recordId) {
        FinanceRecord record = financeRecordMapper.selectByPrimaryKey(recordId, userId);
        if (record == null) {
            return null;
        }
        return convertToVO(record);
    }

    @Override
    public List<RecordVO> getRecords(Long userId, LocalDate startDate, LocalDate endDate, Integer type, Long categoryId, Long accountId) {
        List<FinanceRecord> records = financeRecordMapper.selectByUserIdAndDateRange(userId, startDate, endDate, type, categoryId, accountId);
        return records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void uploadVoucher(Long userId, Long recordId, String voucherUrl) {
        FinanceRecord existingRecord = financeRecordMapper.selectByPrimaryKey(recordId, userId);
        if (existingRecord == null) {
            throw new BusinessException("收支记录不存在或无权修改");
        }
        existingRecord.setVoucherUrl(voucherUrl);
        existingRecord.setUpdateTime(LocalDateTime.now());
        int result = financeRecordMapper.updateByPrimaryKeySelective(existingRecord);
        if (result != 1) {
            throw new BusinessException("上传凭证失败");
        }
    }

    private RecordVO convertToVO(FinanceRecord record) {
        RecordVO vo = new RecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setTypeName(record.getType() == 1 ? "收入" : "支出");

        // 填充账户名称
        Account account = accountMapper.selectByPrimaryKey(record.getAccountId(), record.getUserId());
        if (account != null) {
            vo.setAccountName(account.getAccountName());
        }

        // 填充类别名称
        FinanceCategory category = financeCategoryMapper.selectByPrimaryKey(record.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getCategoryName());
        }
        return vo;
    }
}