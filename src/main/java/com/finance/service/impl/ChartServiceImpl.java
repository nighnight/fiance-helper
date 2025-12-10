package com.finance.service.impl;

import com.finance.mapper.FinanceRecordMapper;
import com.finance.po.SysUser;
import com.finance.service.ChartService;
import com.finance.vo.ChartVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChartServiceImpl implements ChartService {

    @Autowired
    private FinanceRecordMapper recordMapper;

    @Override
    public Map<String, Object> getAnalysisData(String month, HttpSession session) {
        SysUser user = (SysUser) session.getAttribute("USER_SESSION");
        Long userId = user.getId();

        // 如果前端没传月份，默认查当前月
        if (month == null || month.isEmpty()) {
            month = LocalDate.now().toString().substring(0, 7);
        }

        Map<String, Object> result = new HashMap<>();

        // 1. 获取饼图数据 (分类支出)
        List<ChartVO> pieData = recordMapper.selectCategoryExpenseStats(userId, month);
        result.put("pieData", pieData);

        // 2. 获取折线图数据 (每日支出)
        List<ChartVO> lineDataExpense = recordMapper.selectDailyStats(userId, month, 2);
        result.put("lineExpense", lineDataExpense);

        // 3. 获取折线图数据 (每日收入)
        List<ChartVO> lineDataIncome = recordMapper.selectDailyStats(userId, month, 1);
        result.put("lineIncome", lineDataIncome);

        return result;
    }
}