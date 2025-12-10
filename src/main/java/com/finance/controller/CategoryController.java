package com.finance.controller;

import com.finance.dto.CategoryDTO;
import com.finance.po.FinanceCategory;
import com.finance.service.FinanceCategoryService;
import com.finance.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
@Tag(name = "收支分类管理")
public class CategoryController {

    @Autowired
    private FinanceCategoryService categoryService;

    // ==================== 页面跳转 ====================

    @Operation(summary = "页面：分类列表", hidden = true)
    @GetMapping("/index")
    public String indexPage() {
        return "category/list";
    }

    @Operation(summary = "页面：新增分类", hidden = true)
    @GetMapping("/toAdd")
    public String addPage() {
        return "category/add";
    }

    @Operation(summary = "页面：编辑分类", hidden = true)
    @GetMapping("/toEdit")
    public String editPage() {
        return "category/edit";
    }

    // ==================== API 接口 ====================

    @Operation(summary = "查询分类列表")
    @GetMapping("/list")
    @ResponseBody
    public Result<List<FinanceCategory>> list(HttpSession session) {
        return Result.success(categoryService.getList(session));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    @ResponseBody
    public Result add(@RequestBody CategoryDTO dto, HttpSession session) {
        categoryService.addCategory(dto, session);
        return Result.success();
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    @ResponseBody
    public Result<FinanceCategory> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @Operation(summary = "修改分类")
    @PutMapping
    @ResponseBody
    public Result update(@RequestBody CategoryDTO dto) {
        try {
            categoryService.updateCategory(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}