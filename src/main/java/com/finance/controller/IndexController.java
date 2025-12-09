package com.finance.controller;

import com.finance.po.SysUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        // 从Session获取用户信息
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        model.addAttribute("user", user);
        return "index"; // 对应 templates/index.html
    }
}