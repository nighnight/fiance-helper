package com.finance.service;

import com.finance.dto.CategoryDTO;
import com.finance.po.FinanceCategory;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public interface FinanceCategoryService {
    List<FinanceCategory> getList(HttpSession session);
    void addCategory(CategoryDTO dto, HttpSession session);
    FinanceCategory getById(Long id);
    void updateCategory(CategoryDTO dto);
    void deleteCategory(Long id);
}