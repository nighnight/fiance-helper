package com.finance.controller;

import com.finance.dto.AccountDTO;
import com.finance.po.Account;
import com.finance.service.AccountService;
import com.finance.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/account")
@Tag(name = "账户管理")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // ==================== 页面跳转 (View) ====================

    @Operation(summary = "页面：账户列表", hidden = true)
    @GetMapping("/index")
    public String indexPage() {
        return "account/list";
    }

    @Operation(summary = "页面：新增账户", hidden = true)
    @GetMapping("/toAdd")
    public String addPage() {
        return "account/add";
    }

    @Operation(summary = "页面：编辑账户", hidden = true)
    @GetMapping("/toEdit")
    public String editPage() {
        return "account/edit";
    }

    // ==================== 数据接口 (API) ====================

    @Operation(summary = "查询我的账户列表")
    @GetMapping("/list")
    @ResponseBody
    public Result<List<Account>> list(HttpSession session) {
        List<Account> list = accountService.getList(session);
        return Result.success(list);
    }

    @Operation(summary = "新增账户")
    @PostMapping
    @ResponseBody
    public Result add(@RequestBody AccountDTO accountDTO, HttpSession session) {
        accountService.addAccount(accountDTO, session);
        return Result.success();
    }

    @Operation(summary = "根据ID查询账户")
    @GetMapping("/{id}")
    @ResponseBody
    public Result<Account> getById(@PathVariable Long id) {
        Account account = accountService.getById(id);
        return Result.success(account);
    }

    @Operation(summary = "修改账户")
    @PutMapping
    @ResponseBody
    public Result update(@RequestBody AccountDTO accountDTO) {
        accountService.updateAccount(accountDTO);
        return Result.success();
    }

    @Operation(summary = "删除账户")
    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return Result.success();
    }
}