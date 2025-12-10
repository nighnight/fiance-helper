package com.finance.service;

import com.finance.vo.ChartVO;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.List;

public interface ChartService {
    Map<String, Object> getAnalysisData(String month, HttpSession session);
}