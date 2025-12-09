package com.finance.service;

import com.finance.dto.BudgetDTO;
import com.finance.po.Budget;
import com.finance.vo.BudgetVO;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {
    void addBudget(Long userId, BudgetDTO budgetDTO);
    void updateBudget(Long userId, BudgetDTO budgetDTO);
    void deleteBudget(Long userId, Long budgetId);
    BudgetVO getBudgetById(Long userId, Long budgetId);
    List<BudgetVO> getCurrentBudgets(Long userId, Integer cycleType, String cycleValue);
    List<BudgetVO> getAllBudgets(Long userId); // 获取用户所有预算
    void updateBudgetUsedAmount(Long userId, Long categoryId, Integer cycleType, String cycleValue, BigDecimal amountDelta);
}