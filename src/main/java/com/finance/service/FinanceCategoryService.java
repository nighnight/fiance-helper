package com.finance.service;

import com.finance.dto.CategoryDTO;
import com.finance.po.FinanceCategory;
import com.finance.vo.CategoryVO;

import java.util.List;

public interface FinanceCategoryService {
    void addCategory(Long userId, CategoryDTO categoryDTO);
    void updateCategory(Long userId, CategoryDTO categoryDTO);
    void deleteCategory(Long userId, Long categoryId);
    CategoryVO getCategoryById(Long categoryId);
    List<CategoryVO> getCategoriesByUserIdAndType(Long userId, Integer type);
    List<CategoryVO> getAllPossibleCategoriesForUser(Long userId, Integer type); // 用户自定义+系统默认
}