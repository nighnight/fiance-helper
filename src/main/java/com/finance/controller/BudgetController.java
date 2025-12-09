package com.finance.controller;

import com.finance.dto.BudgetDTO;
import com.finance.exception.BusinessException;
import com.finance.service.BudgetService;
import com.finance.service.FinanceCategoryService;
import com.finance.util.DateUtil;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.BudgetVO;
import com.finance.vo.CategoryVO;
import com.finance.vo.LoginUserVO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;
    @Autowired
    private FinanceCategoryService financeCategoryService; // 用于获取类别信息
    @Autowired
    private HttpSession session;

    private Long getUserId() {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException("用户未登录或会话已过期");
        }
        return loginUser.getId();
    }

    /**
     * 跳转预算列表页面
     * @param model Model
     * @return "budget/list"
     */
    @GetMapping("/list")
    public String budgetListPage(Model model) {
        // 默认显示当前月度的预算
        String currentMonth = DateUtil.formatYearMonth(LocalDate.now());
        model.addAttribute("currentMonth", currentMonth);
        return "budget/list";
    }

    /**
     * 获取当前周期内的预算列表API
     * @param cycleType 周期类型 (1-月度，2-季度，3-年度)
     * @param cycleValue 周期值 (如: "2024-10")
     * @return JSON结果
     */
    @GetMapping("/api/current")
    @ResponseBody
    public Result<List<BudgetVO>> getCurrentBudgets(
            @RequestParam(defaultValue = "1") Integer cycleType, // 默认月度
            @RequestParam String cycleValue) {
        Long userId = getUserId();
        List<BudgetVO> budgets = budgetService.getCurrentBudgets(userId, cycleType, cycleValue);
        return ResultUtil.success(budgets);
    }

    /**
     * 跳转添加预算页面
     * @param model Model
     * @return "budget/add"
     */
    @GetMapping("/add")
    public String addBudgetPage(Model model) {
        Long userId = getUserId();
        // 获取所有支出类别（预算只针对支出）
        List<CategoryVO> expenseCategories = financeCategoryService.getAllPossibleCategoriesForUser(userId, 2);
        model.addAttribute("expenseCategories", expenseCategories);
        model.addAttribute("budgetDTO", new BudgetDTO());
        model.addAttribute("currentMonth", DateUtil.formatYearMonth(LocalDate.now())); // 默认当前月份
        return "budget/add";
    }

    /**
     * 添加预算API
     * @param budgetDTO 预算DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/add")
    @ResponseBody
    public Result<String> addBudget(@Valid @RequestBody BudgetDTO budgetDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Long userId = getUserId();
        budgetService.addBudget(userId, budgetDTO);
        return ResultUtil.success("预算添加成功");
    }

    /**
     * 跳转编辑预算页面
     * @param id 预算ID
     * @param model Model
     * @return "budget/edit"
     */
    @GetMapping("/edit/{id}")
    public String editBudgetPage(@PathVariable Long id, Model model) {
        Long userId = getUserId();
        BudgetVO budgetVO = budgetService.getBudgetById(userId, id);
        if (budgetVO == null) {
            throw new BusinessException("预算不存在");
        }
        BudgetDTO budgetDTO = new BudgetDTO();
        BeanUtils.copyProperties(budgetVO, budgetDTO);
        // 如果是总预算，categoryId保持0L
        // categoryName, cycleTypeName等到前端页面再处理
        model.addAttribute("budgetDTO", budgetDTO);
        model.addAttribute("budgetId", id);
        // 获取所有支出类别，用于下拉选择
        List<CategoryVO> expenseCategories = financeCategoryService.getAllPossibleCategoriesForUser(userId, 2);
        model.addAttribute("expenseCategories", expenseCategories);
        return "budget/edit";
    }

    /**
     * 更新预算API
     * @param id 预算ID
     * @param budgetDTO 预算DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/update/{id}")
    @ResponseBody
    public Result<String> updateBudget(@PathVariable Long id, @Valid @RequestBody BudgetDTO budgetDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Long userId = getUserId();
        budgetDTO.setId(id);
        budgetService.updateBudget(userId, budgetDTO);
        return ResultUtil.success("预算更新成功");
    }

    /**
     * 删除预算API
     * @param id 预算ID
     * @return JSON结果
     */
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public Result<String> deleteBudget(@PathVariable Long id) {
        Long userId = getUserId();
        budgetService.deleteBudget(userId, id);
        return ResultUtil.success("预算删除成功");
    }
}