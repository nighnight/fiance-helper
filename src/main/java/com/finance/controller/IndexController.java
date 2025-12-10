package com.finance.controller;

import com.finance.mapper.AccountMapper;
import com.finance.mapper.FinanceRecordMapper;
import com.finance.po.Account;
import com.finance.po.FinanceRecord;
import com.finance.po.SysUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private FinanceRecordMapper recordMapper;

    @Autowired
    private AccountMapper accountMapper;

    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        model.addAttribute("user", user);

        // 1. 获取当前月份 (格式: 2023-12)
        String currentMonth = LocalDate.now().toString().substring(0, 7);

        // 2. 查询本月所有记录
        List<FinanceRecord> records = recordMapper.selectByMonth(user.getId(), currentMonth);

        // 3. 计算总收入 (Type=1) 和 总支出 (Type=2)
        BigDecimal totalIncome = records.stream()
                .filter(r -> r.getType() == 1)
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = records.stream()
                .filter(r -> r.getType() == 2)
                .map(FinanceRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. 计算总资产 (查所有账户余额之和)
        List<Account> accounts = accountMapper.selectByUserId(user.getId());
        BigDecimal totalAssets = accounts.stream()
                .map(Account::getCurrentBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. 存入 Model 传给前端
        model.addAttribute("monthIncome", totalIncome);
        model.addAttribute("monthExpense", totalExpense);
        model.addAttribute("totalAssets", totalAssets);

        return "index";
    }
}