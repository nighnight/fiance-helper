package com.finance.controller;

import com.finance.exception.BusinessException;
import com.finance.service.ChartService;
import com.finance.util.DateUtil;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.KeyIndexVO;
import com.finance.vo.LoginUserVO;
import com.finance.vo.PieVO;
import com.finance.vo.TrendVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/chart")
public class ChartController {

    @Autowired
    private ChartService chartService;
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
     * 跳转图表分析页面
     * @return "chart/analysis"
     */
    @GetMapping("/analysis")
    public String analysisPage(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("currentMonth", DateUtil.formatYearMonth(today));
        return "chart/analysis";
    }

    /**
     * 获取月度收支趋势数据API
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return JSON结果
     */
    @GetMapping("/api/monthlyTrend")
    @ResponseBody
    public Result<List<TrendVO>> getMonthlyTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Long userId = getUserId();
        List<TrendVO> data = chartService.getMonthlyTrend(userId, startDate, endDate);
        return ResultUtil.success(data);
    }

    /**
     * 获取支出类别饼图数据API
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return JSON结果
     */
    @GetMapping("/api/expenseCategoryPie")
    @ResponseBody
    public Result<List<PieVO>> getExpenseCategoryPie(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Long userId = getUserId();
        List<PieVO> data = chartService.getExpenseCategoryPieData(userId, startDate, endDate);
        return ResultUtil.success(data);
    }

    /**
     * 获取收入类别饼图数据API
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return JSON结果
     */
    @GetMapping("/api/incomeCategoryPie")
    @ResponseBody
    public Result<List<PieVO>> getIncomeCategoryPie(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        Long userId = getUserId();
        List<PieVO> data = chartService.getIncomeCategoryPieData(userId, startDate, endDate);
        return ResultUtil.success(data);
    }

    /**
     * 获取关键财务指标API
     * @param dateStr 用于确定当月的日期字符串（如 "yyyy-MM-dd"）
     * @return JSON结果
     */
    @GetMapping("/api/keyIndex")
    @ResponseBody
    public Result<KeyIndexVO> getKeyFinancialIndex(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        Long userId = getUserId();
        KeyIndexVO data = chartService.getKeyFinancialIndex(userId, date);
        return ResultUtil.success(data);
    }
}