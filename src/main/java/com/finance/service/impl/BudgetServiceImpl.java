package com.finance.service.impl;

import com.finance.dto.BudgetDTO;
import com.finance.mapper.BudgetMapper;
import com.finance.mapper.FinanceRecordMapper;
import com.finance.po.Budget;
import com.finance.po.FinanceRecord;
import com.finance.po.SysUser;
import com.finance.service.BudgetService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired private BudgetMapper budgetMapper;
    @Autowired private FinanceRecordMapper recordMapper; // 需要用到记录Mapper来统计花费

    @Override
    public List<Budget> getList(String month, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        if (month == null || month.isEmpty()) {
            month = LocalDate.now().toString().substring(0, 7); // 默认当前月
        }

        List<Budget> budgets = budgetMapper.selectList(user.getId(), month);

        // 实时计算已使用金额 (这里比较简单粗暴，实际项目可能写SQL聚合更高效)
        // 1. 查出本月所有支出记录
        List<FinanceRecord> records = recordMapper.selectByMonth(user.getId(), month);

        // 2. 遍历预算列表，计算进度
        for (Budget b : budgets) {
            BigDecimal used = BigDecimal.ZERO;
            for (FinanceRecord r : records) {
                // 如果预算是分类预算(id!=0)，且记录是支出(type=2)，且分类ID匹配
                if (b.getCategoryId().equals(r.getCategoryId()) && r.getType() == 2) {
                    used = used.add(r.getAmount());
                }
            }
            b.setUsedAmount(used);
        }
        return budgets;
    }

    @Override
    public void saveBudget(BudgetDTO dto, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");

        // 检查该月该分类是否已设置预算
        Budget exist = budgetMapper.selectUnique(user.getId(), dto.getCategoryId(), dto.getCycleValue());

        if (exist != null) {
            // 如果已存在，则更新金额
            exist.setBudgetAmount(dto.getBudgetAmount());
            budgetMapper.update(exist);
        } else {
            // 不存在则新增
            Budget budget = new Budget();
            BeanUtils.copyProperties(dto, budget);
            budget.setUserId(user.getId());
            budget.setCycleType(1); // 默认月度
            budget.setUsedAmount(BigDecimal.ZERO);
            budgetMapper.insert(budget);
        }
    }

    @Override
    public void deleteBudget(Long id) {
        budgetMapper.deleteById(id);
    }
}