package com.finance.controller;

import com.finance.dto.BudgetDTO;
import com.finance.po.Budget;
import com.finance.service.BudgetService;
import com.finance.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/budget")
@Tag(name = "预算管理")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    // === View ===
    @GetMapping("/index")
    public String indexPage() {
        return "budget/list";
    }

    // === API ===
    @GetMapping("/list")
    @ResponseBody
    public Result<List<Budget>> list(@RequestParam(required = false) String month, HttpSession session) {
        return Result.success(budgetService.getList(month, session));
    }

    @PostMapping("/save")
    @ResponseBody
    public Result save(@RequestBody BudgetDTO dto, HttpSession session) {
        try {
            budgetService.saveBudget(dto, session);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return Result.success();
    }
}