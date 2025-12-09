package com.finance.service.impl;

import com.finance.dto.BudgetDTO;
import com.finance.exception.BusinessException;
import com.finance.mapper.BudgetMapper;
import com.finance.mapper.FinanceCategoryMapper;
import com.finance.po.Budget;
import com.finance.po.FinanceCategory;
import com.finance.service.BudgetService;
import com.finance.util.DateUtil;
import com.finance.vo.BudgetVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetMapper budgetMapper;
    @Autowired
    private FinanceCategoryMapper financeCategoryMapper;

    @Override
    @Transactional
    public void addBudget(Long userId, BudgetDTO budgetDTO) {
        // 1. 检查类别是否存在 (如果是具体类别预算)
        if (!budgetDTO.getCategoryId().equals(0L)) { // 0表示总预算，不需要检查具体类别
            FinanceCategory category = financeCategoryMapper.selectByPrimaryKey(budgetDTO.getCategoryId());
            if (category == null || (!category.getUserId().equals(0L) && !category.getUserId().equals(userId))) {
                throw new BusinessException("预算类别不存在或不属于您");
            }
            if (category.getType() == 1) { // 收入类别不能设置支出预算
                throw new BusinessException("只能为支出类别设置预算");
            }
        }

        // 2. 检查同一周期是否存在相同类别的预算
        Budget existingBudget = budgetMapper.selectByUniqueKey(userId, budgetDTO.getCategoryId(), budgetDTO.getCycleType(), budgetDTO.getCycleValue());
        if (existingBudget != null) {
            throw new BusinessException("同一周期下该类别已存在预算，请勿重复添加");
        }

        // 3. 插入预算
        Budget budget = new Budget();
        BeanUtils.copyProperties(budgetDTO, budget);
        budget.setUserId(userId);
        budget.setUsedAmount(BigDecimal.ZERO); // 新增预算时，已使用金额为0
        budget.setCreateTime(LocalDateTime.now());
        budget.setUpdateTime(LocalDateTime.now());

        int result = budgetMapper.insert(budget);
        if (result != 1) {
            throw new BusinessException("添加预算失败");
        }
    }

    @Override
    @Transactional
    public void updateBudget(Long userId, BudgetDTO budgetDTO) {
        if (budgetDTO.getId() == null) {
            throw new BusinessException("预算ID不能为空");
        }
        Budget existingBudget = budgetMapper.selectByPrimaryKey(budgetDTO.getId(), userId);
        if (existingBudget == null) {
            throw new BusinessException("预算不存在或无权修改");
        }

        // 检查类别是否存在 (如果是具体类别预算)
        if (!budgetDTO.getCategoryId().equals(0L)) {
            FinanceCategory category = financeCategoryMapper.selectByPrimaryKey(budgetDTO.getCategoryId());
            if (category == null || (!category.getUserId().equals(0L) && !category.getUserId().equals(userId))) {
                throw new BusinessException("预算类别不存在或不属于您");
            }
            if (category.getType() == 1) { // 收入类别不能设置支出预算
                throw new BusinessException("只能为支出类别设置预算");
            }
        }

        // 检查修改后的唯一性
        if (!existingBudget.getCategoryId().equals(budgetDTO.getCategoryId()) ||
                !existingBudget.getCycleType().equals(budgetDTO.getCycleType()) ||
                !existingBudget.getCycleValue().equals(budgetDTO.getCycleValue())) {
            Budget conflictBudget = budgetMapper.selectByUniqueKey(userId, budgetDTO.getCategoryId(), budgetDTO.getCycleType(), budgetDTO.getCycleValue());
            if (conflictBudget != null && !conflictBudget.getId().equals(budgetDTO.getId())) {
                throw new BusinessException("同一周期下该类别已存在预算，请检查");
            }
        }

        existingBudget.setCategoryId(budgetDTO.getCategoryId());
        existingBudget.setCycleType(budgetDTO.getCycleType());
        existingBudget.setCycleValue(budgetDTO.getCycleValue());
        existingBudget.setBudgetAmount(budgetDTO.getBudgetAmount());
        existingBudget.setUpdateTime(LocalDateTime.now());

        int result = budgetMapper.updateByPrimaryKeySelective(existingBudget);
        if (result != 1) {
            throw new BusinessException("更新预算失败");
        }
    }

    @Override
    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget existingBudget = budgetMapper.selectByPrimaryKey(budgetId, userId);
        if (existingBudget == null) {
            throw new BusinessException("预算不存在或无权删除");
        }
        int result = budgetMapper.deleteByPrimaryKey(budgetId, userId);
        if (result != 1) {
            throw new BusinessException("删除预算失败");
        }
    }

    @Override
    public BudgetVO getBudgetById(Long userId, Long budgetId) {
        Budget budget = budgetMapper.selectByPrimaryKey(budgetId, userId);
        if (budget == null) {
            return null;
        }
        return convertToVO(budget);
    }

    @Override
    public List<BudgetVO> getCurrentBudgets(Long userId, Integer cycleType, String cycleValue) {
        List<Budget> budgets = budgetMapper.selectByUserIdAndCycle(userId, cycleType, cycleValue);
        return budgets.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<BudgetVO> getAllBudgets(Long userId) {
        List<Budget> budgets = budgetMapper.selectByUserId(userId);
        return budgets.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateBudgetUsedAmount(Long userId, Long categoryId, Integer cycleType, String cycleValue, BigDecimal amountDelta) {
        // 先尝试查询现有预算
        Budget budget = budgetMapper.selectByUniqueKey(userId, categoryId, cycleType, cycleValue);

        if (budget != null) {
            // 如果存在，则更新
            budgetMapper.updateUsedAmount(budget.getId(), amountDelta);
        } else {
            // 如果不存在，通常情况下，收支发生时，如果对应周期和类别没有预算，则不创建
            // 但是如果是为了实现“实时显示即使没有设置预算也计算已使用金额”，则需要创建零预算的预算记录
            // 为简化，这里暂时只更新现有预算
            // 或者： 如果需要，可以在这里增加一个逻辑：如果不存在，并且amountDelta > 0, 可以考虑自动创建一个空预算
            // log.warn("用户 {} 在 {} {} 周期下，针对类别 {} 没有设置预算，但发生了支出。", userId, cycleValue, cycleType, categoryId);
        }
    }

    private BudgetVO convertToVO(Budget budget) {
        BudgetVO vo = new BudgetVO();
        BeanUtils.copyProperties(budget, vo);

        // 填充类别名称
        if (budget.getCategoryId().equals(0L)) {
            vo.setCategoryName("总预算");
        } else {
            FinanceCategory category = financeCategoryMapper.selectByPrimaryKey(budget.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getCategoryName());
            } else {
                vo.setCategoryName("未知类别"); // 防止类别被删除的情况
            }
        }

        // 填充周期类型名称
        switch (budget.getCycleType()) {
            case 1: vo.setCycleTypeName("月度"); break;
            case 2: vo.setCycleTypeName("季度"); break;
            case 3: vo.setCycleTypeName("年度"); break;
            default: vo.setCycleTypeName("未知");
        }

        // 计算剩余金额和使用进度
        vo.setRemainingAmount(budget.getBudgetAmount().subtract(budget.getUsedAmount()));
        if (budget.getBudgetAmount().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal progress = budget.getUsedAmount().divide(budget.getBudgetAmount(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            vo.setProgress(progress);
        } else {
            vo.setProgress(BigDecimal.ZERO); // 如果预算金额为0，进度为0
        }
        return vo;
    }
}