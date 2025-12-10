package com.finance.service.impl;

import com.finance.dto.CategoryDTO;
import com.finance.mapper.FinanceCategoryMapper;
import com.finance.po.FinanceCategory;
import com.finance.po.SysUser;
import com.finance.service.FinanceCategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanceCategoryServiceImpl implements FinanceCategoryService {

    @Autowired
    private FinanceCategoryMapper categoryMapper;

    @Override
    public List<FinanceCategory> getList(HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        return categoryMapper.selectByUserId(user.getId());
    }

    @Override
    public void addCategory(CategoryDTO dto, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        FinanceCategory category = new FinanceCategory();
        BeanUtils.copyProperties(dto, category);

        category.setUserId(user.getId()); // 设置为当前用户
        category.setIsDefault(0); // 用户自定义的不是系统默认
        if(category.getSort() == null) category.setSort(100); // 默认排序

        categoryMapper.insert(category);
    }

    @Override
    public FinanceCategory getById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public void updateCategory(CategoryDTO dto) {
        // 校验：不能修改系统默认分类（user_id=0）
        FinanceCategory old = categoryMapper.selectById(dto.getId());
        if(old != null && old.getIsDefault() == 1) {
            throw new RuntimeException("系统默认分类不可修改");
        }

        FinanceCategory category = new FinanceCategory();
        BeanUtils.copyProperties(dto, category);
        categoryMapper.update(category);
    }

    @Override
    public void deleteCategory(Long id) {
        // 校验：不能删除系统默认分类
        FinanceCategory old = categoryMapper.selectById(id);
        if(old != null && old.getIsDefault() == 1) {
            throw new RuntimeException("系统默认分类不可删除");
        }
        categoryMapper.deleteById(id);
    }
}