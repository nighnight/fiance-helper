package com.finance.service;

import com.finance.dto.BudgetDTO;
import com.finance.po.Budget;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public interface BudgetService {
    List<Budget> getList(String month, HttpSession session);
    void saveBudget(BudgetDTO dto, HttpSession session);
    void deleteBudget(Long id);
}