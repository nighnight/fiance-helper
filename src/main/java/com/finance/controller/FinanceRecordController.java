package com.finance.controller;

import com.finance.dto.FinanceRecordDTO;
import com.finance.exception.BusinessException;
import com.finance.service.FinanceRecordService;
import com.finance.util.DateUtil;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.LoginUserVO;
import com.finance.vo.RecordVO;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/record")
public class FinanceRecordController {


}