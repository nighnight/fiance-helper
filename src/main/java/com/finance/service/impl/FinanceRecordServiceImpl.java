package com.finance.service.impl;

import com.finance.dto.FinanceRecordDTO;
import com.finance.mapper.AccountMapper;
import com.finance.mapper.FinanceRecordMapper;
import com.finance.po.Account;
import com.finance.po.FinanceRecord;
import com.finance.po.SysUser;
import com.finance.service.FinanceRecordService;
import com.finance.util.ExcelUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class FinanceRecordServiceImpl implements FinanceRecordService {

    @Autowired
    private FinanceRecordMapper recordMapper;
    @Autowired
    private AccountMapper accountMapper;

    @Override
    public List<FinanceRecord> getList(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        return recordMapper.selectList(user.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 事务控制：记账失败则回滚余额
    public void addRecord(FinanceRecordDTO dto, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");

        // 1. 保存记录
        FinanceRecord record = new FinanceRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(user.getId());
        recordMapper.insert(record);

        // 2. 更新账户余额（收入加钱，支出减钱）
        updateAccountBalance(dto.getAccountId(), dto.getAmount(), dto.getType());
    }

    @Override
    public FinanceRecord getById(Long id) {
        return recordMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecord(FinanceRecordDTO dto) {
        // 1. 查出旧记录
        FinanceRecord oldRecord = recordMapper.selectById(dto.getId());
        if (oldRecord == null) throw new RuntimeException("记录不存在");

        // 2. 回滚旧余额（反向操作：如果是收入就减回去，是支出就加回去）
        // 逻辑：旧记录类型为1(收入)，回滚时传2(减)；旧记录类型为2(支出)，回滚时传1(加)
        int revertType = (oldRecord.getType() == 1) ? 2 : 1;
        updateAccountBalance(oldRecord.getAccountId(), oldRecord.getAmount(), revertType);

        // 3. 应用新余额
        updateAccountBalance(dto.getAccountId(), dto.getAmount(), dto.getType());

        // 4. 更新记录信息
        FinanceRecord updateRecord = new FinanceRecord();
        BeanUtils.copyProperties(dto, updateRecord);
        recordMapper.update(updateRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecord(Long id) {
        // 1. 查出记录
        FinanceRecord record = recordMapper.selectById(id);
        if (record == null) return;

        // 2. 回滚余额（删除收入要扣钱，删除支出要还钱）
        int revertType = (record.getType() == 1) ? 2 : 1;
        updateAccountBalance(record.getAccountId(), record.getAmount(), revertType);

        // 3. 删除记录
        recordMapper.deleteById(id);
    }

    /**
     * 内部工具：更新账户余额
     * @param accountId 账户ID
     * @param amount 金额
     * @param type 1-增加余额，2-减少余额
     */
    private void updateAccountBalance(Long accountId, BigDecimal amount, Integer type) {
        Account account = accountMapper.selectById(accountId);
<<<<<<< HEAD
        if (account == null) throw new BusinessException("账户不存在");

        BigDecimal current = account.getCurrentBalance();
        if (type == 1) {
            current = current.add(amount);
        } else {
            // Bug-01 修复：支出前校验余额是否充足
            if (current.compareTo(amount) < 0) {
                throw new BusinessException("账户余额不足，无法完成支出");
            }
            current = current.subtract(amount);
        }
        account.setCurrentBalance(current);
        accountMapper.update(account);
    }
    @Override
    public ByteArrayInputStream exportRecords(HttpSession session) throws IOException {
        // 1. 获取当前用户的所有记录
        List<FinanceRecord> records = getList(session);

        // 2. 调用工具类生成 Excel 输入流
        return ExcelUtil.recordsToExcel(records);
    }
    @Override
    public PageInfo<FinanceRecord> getList(int pageNum, int pageSize, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");

        // 1. 开启分页 (这是 PageHelper 的核心)
        PageHelper.startPage(pageNum, pageSize);

        // 2. 执行查询 (这行代码和以前一样，不用动)
        List<FinanceRecord> records = recordMapper.selectList(user.getId());

        // 3. 将查询结果封装到 PageInfo 对象中并返回
        return new PageInfo<>(records);
    }
}