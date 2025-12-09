package com.finance.service.impl;

import com.finance.dto.CategoryDTO;
import com.finance.exception.BusinessException;
import com.finance.mapper.FinanceCategoryMapper;
import com.finance.po.FinanceCategory;
import com.finance.service.FinanceCategoryService;
import com.finance.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinanceCategoryServiceImpl implements FinanceCategoryService {

    @Autowired
    private FinanceCategoryMapper financeCategoryMapper;

    @Override
    @Transactional
    public void addCategory(Long userId, CategoryDTO categoryDTO) {
        // 检查用户是否已存在同名同类型类别
        FinanceCategory existingCategory = financeCategoryMapper.selectByUserIdCategoryNameAndType(userId, categoryDTO.getCategoryName(), categoryDTO.getType());
        if (existingCategory != null) {
            throw new BusinessException("已存在同名的" + (categoryDTO.getType() == 1 ? "收入" : "支出") + "类别");
        }

        FinanceCategory category = new FinanceCategory();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUserId(userId);
        category.setIsDefault(0); // 用户自定义类别
        if (category.getSort() == null) {
            category.setSort(0); // 默认排序
        }
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());

        int result = financeCategoryMapper.insert(category);
        if (result != 1) {
            throw new BusinessException("添加类别失败");
        }
    }

    @Override
    @Transactional
    public void updateCategory(Long userId, CategoryDTO categoryDTO) {
        if (categoryDTO.getId() == null) {
            throw new BusinessException("类别ID不能为空");
        }
        FinanceCategory existingCategory = financeCategoryMapper.selectByPrimaryKey(categoryDTO.getId());
        if (existingCategory == null) {
            throw new BusinessException("类别不存在");
        }
        if (!existingCategory.getUserId().equals(userId) || existingCategory.getIsDefault() == 1) {
            throw new BusinessException("无权修改此类别或无法修改系统默认类别");
        }

        // 检查修改后的名称是否与其他类别冲突
        if (!existingCategory.getCategoryName().equals(categoryDTO.getCategoryName())) {
            FinanceCategory conflictCategory = financeCategoryMapper.selectByUserIdCategoryNameAndType(userId, categoryDTO.getCategoryName(), categoryDTO.getType());
            if (conflictCategory != null && !conflictCategory.getId().equals(categoryDTO.getId())) {
                throw new BusinessException("已存在同名的" + (categoryDTO.getType() == 1 ? "收入" : "支出") + "类别");
            }
        }

        existingCategory.setCategoryName(categoryDTO.getCategoryName());
        existingCategory.setType(categoryDTO.getType());
        existingCategory.setSort(categoryDTO.getSort() == null ? 0 : categoryDTO.getSort());
        existingCategory.setUpdateTime(LocalDateTime.now());

        int result = financeCategoryMapper.updateByPrimaryKeySelective(existingCategory);
        if (result != 1) {
            throw new BusinessException("更新类别失败");
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        FinanceCategory existingCategory = financeCategoryMapper.selectByPrimaryKey(categoryId);
        if (existingCategory == null) {
            throw new BusinessException("类别不存在");
        }
        if (!existingCategory.getUserId().equals(userId) || existingCategory.getIsDefault() == 1) {
            throw new BusinessException("无权删除此类别或无法删除系统默认类别");
        }
        // TODO: 删除类别时，需要考虑此类别下的收支记录如何处理
        int result = financeCategoryMapper.deleteByPrimaryKey(categoryId, userId);
        if (result != 1) {
            throw new BusinessException("删除类别失败");
        }
    }

    @Override
    public CategoryVO getCategoryById(Long categoryId) {
        FinanceCategory category = financeCategoryMapper.selectByPrimaryKey(categoryId);
        if (category == null) {
            return null;
        }
        return convertToVO(category);
    }

    @Override
    public List<CategoryVO> getCategoriesByUserIdAndType(Long userId, Integer type) {
        List<FinanceCategory> categories = financeCategoryMapper.selectByUserIdAndType(userId, type);
        return categories.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getAllPossibleCategoriesForUser(Long userId, Integer type) {
        List<FinanceCategory> defaultCategories = financeCategoryMapper.selectDefaultCategoriesByType(type);
        List<FinanceCategory> userCategories = financeCategoryMapper.selectByUserIdAndType(userId, type);

        List<FinanceCategory> allCategories = new ArrayList<>(defaultCategories);
        // 合并用户自定义类别，如果用户自定义类别与默认类别重名，通常以用户自定义为准
        // 但数据库设计中uk_user_name_type已经限制了用户不能与自己的类别重名，但可以与系统类别重名
        // 这里简单合并，前端展示时可能需要区分或去重
        for (FinanceCategory userCat : userCategories) {
            boolean found = false;
            for (FinanceCategory defaultCat : defaultCategories) {
                if (userCat.getCategoryName().equals(defaultCat.getCategoryName()) && userCat.getType().equals(defaultCat.getType())) {
                    found = true; // 理论上不会发生，因为用户ID不同
                    break;
                }
            }
            if (!found) { // 避免与系统默认类别重复（按名称和类型），但用户自定义会有自己的ID
                allCategories.add(userCat);
            }
        }
        return allCategories.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private CategoryVO convertToVO(FinanceCategory category) {
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        vo.setTypeName(category.getType() == 1 ? "收入" : "支出");
        return vo;
    }
}