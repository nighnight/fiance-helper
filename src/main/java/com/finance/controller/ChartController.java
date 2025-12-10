package com.finance.controller;

import com.finance.service.ChartService;
import com.finance.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/chart")
@Tag(name = "统计报表")
public class ChartController {

    @Autowired
    private ChartService chartService;

    // 页面跳转
    @GetMapping("/index")
    public String indexPage() {
        return "chart/analysis";
    }

    // 获取图表数据接口
    @Operation(summary = "获取统计数据")
    @GetMapping("/data")
    @ResponseBody
    public Result<Map<String, Object>> getData(@RequestParam(required = false) String month, HttpSession session) {
        return Result.success(chartService.getAnalysisData(month, session));
    }
}