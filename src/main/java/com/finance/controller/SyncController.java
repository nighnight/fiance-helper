package com.finance.controller;

import com.finance.exception.BusinessException;
import com.finance.mapper.FinanceRecordMapper; // 用于获取最新ID
import com.finance.po.DataSync;
import com.finance.service.SyncService;
import com.finance.util.Result;
import com.finance.util.ResultUtil;
import com.finance.vo.LoginUserVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/sync")
public class SyncController {

}