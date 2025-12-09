package com.finance.controller;

import com.finance.dto.CategoryDTO;
import com.finance.exception.BusinessException;
import com.finance.service.FinanceCategoryService;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
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

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private FinanceCategoryService financeCategoryService;
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
     * 跳转类别列表页面
     * @param model Model
     * @return "category/list"
     */
    @GetMapping("/list")
    public String categoryListPage(Model model) {
        Long userId = getUserId();
        List<CategoryVO> incomeCategories = financeCategoryService.getAllPossibleCategoriesForUser(userId, 1); // 收入
        List<CategoryVO> expenseCategories = financeCategoryService.getAllPossibleCategoriesForUser(userId, 2); // 支出
        model.addAttribute("incomeCategories", incomeCategories);
        model.addAttribute("expenseCategories", expenseCategories);
        return "category/list";
    }

    /**
     * 跳转添加类别页面
     * @param model Model
     * @return "category/add"
     */
    @GetMapping("/add")
    public String addCategoryPage(Model model) {
        model.addAttribute("categoryDTO", new CategoryDTO());
        return "category/add";
    }

    /**
     * 添加类别API
     * @param categoryDTO 类别DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/add")
    @ResponseBody
    public Result<String> addCategory(@Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Long userId = getUserId();
        financeCategoryService.addCategory(userId, categoryDTO);
        return ResultUtil.success("类别添加成功");
    }

    /**
     * 跳转编辑类别页面
     * @param id 类别ID
     * @param model Model
     * @return "category/edit"
     */
    @GetMapping("/edit/{id}")
    public String editCategoryPage(@PathVariable Long id, Model model) {
        CategoryVO categoryVO = financeCategoryService.getCategoryById(id);
        if (categoryVO == null) {
            throw new BusinessException("类别不存在");
        }
        if (categoryVO.getIsDefault() == 1) { // 默认类别不能编辑
            throw new BusinessException("系统默认类别无法编辑");
        }
        CategoryDTO categoryDTO = new CategoryDTO();
        BeanUtils.copyProperties(categoryVO, categoryDTO);
        model.addAttribute("categoryDTO", categoryDTO);
        model.addAttribute("categoryId", id);
        return "category/edit";
    }

    /**
     * 更新类别API
     * @param id 类别ID
     * @param categoryDTO 类别DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/update/{id}")
    @ResponseBody
    public Result<String> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Long userId = getUserId();
        categoryDTO.setId(id);
        financeCategoryService.updateCategory(userId, categoryDTO);
        return ResultUtil.success("类别更新成功");
    }

    /**
     * 删除类别API
     * @param id 类别ID
     * @return JSON结果
     */
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public Result<String> deleteCategory(@PathVariable Long id) {
        Long userId = getUserId();
        financeCategoryService.deleteCategory(userId, id);
        return ResultUtil.success("类别删除成功");
    }

    /**
     * 获取用户所有(自定义+系统默认)收入/支出类别的API (用于下拉选择等场景)
     * @param type 1-收入，2-支出
     * @return JSON结果
     */
    @GetMapping("/api/all/{type}")
    @ResponseBody
    public Result<List<CategoryVO>> getAllCategories(@PathVariable Integer type) {
        Long userId = getUserId();
        List<CategoryVO> categories = financeCategoryService.getAllPossibleCategoriesForUser(userId, type);
        return ResultUtil.success(categories);
    }
}