package com.finance.controller;

import com.finance.dto.BudgetDTO;
import com.finance.exception.BusinessException;
import com.finance.service.BudgetService;
import com.finance.service.FinanceCategoryService;
import com.finance.util.DateUtil;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.BudgetVO;
import com.finance.vo.CategoryVO;
import com.finance.vo.LoginUserVO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/budget")
public class BudgetController {


}