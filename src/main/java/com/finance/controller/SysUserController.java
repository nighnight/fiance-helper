package com.finance.controller;

import com.finance.po.SysUser;
import com.finance.service.SysUserService;
import com.finance.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@Tag(name = "用户管理")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    // ==================== 1. 页面跳转 (GET 请求) ====================

    @Operation(summary = "跳转：登录页", hidden = true)
    @GetMapping("/login")  // 浏览器访问地址: http://localhost:8080/user/login
    public String loginPage() {
        return "user/login";
    }

    @Operation(summary = "跳转：注册页", hidden = true)
    @GetMapping("/register") // 浏览器访问地址: http://localhost:8080/user/register
    public String registerPage() {
        return "user/register";
    }

    // ==================== 2. 数据接口 (POST 请求) ====================

    @Operation(summary = "提交登录")
    @PostMapping("/login") // 前端 Ajax 请求地址: /user/login (必须是 POST)
    @ResponseBody          // 必须加！表示返回 JSON 数据
    public Result<SysUser> doLogin(@RequestBody SysUser user, HttpSession session) {
        return sysUserService.login(user.getUsername(), user.getPassword(), session);
    }

    @Operation(summary = "提交注册")
    @PostMapping("/register") // 前端 Ajax 请求地址: /user/register (必须是 POST)
    @ResponseBody             // 必须加！
    public Result doRegister(@RequestBody SysUser user) {
        return sysUserService.register(user);
    }

    @Operation(summary = "退出登录")
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }
}