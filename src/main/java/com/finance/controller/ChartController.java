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


}