package com.finance.service.impl;

import com.finance.mapper.AccountMapper;
import com.finance.mapper.FinanceCategoryMapper;
import com.finance.mapper.FinanceRecordMapper;
import com.finance.po.FinanceCategory;
import com.finance.service.ChartService;
import com.finance.util.DateUtil;
import com.finance.vo.KeyIndexVO;
import com.finance.vo.PieVO;
import com.finance.vo.TrendVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartServiceImpl implements ChartService {

}