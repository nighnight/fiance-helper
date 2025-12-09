package com.finance.service.impl;

import com.finance.dto.FinanceRecordDTO;
import com.finance.exception.BusinessException;
import com.finance.mapper.AccountMapper;
import com.finance.mapper.FinanceCategoryMapper;
import com.finance.mapper.FinanceRecordMapper;
import com.finance.po.Account;
import com.finance.po.FinanceCategory;
import com.finance.po.FinanceRecord;
import com.finance.service.AccountService; // 注意这里引用了AccountService
import com.finance.service.BudgetService; // 引用BudgetService
import com.finance.service.FinanceRecordService;
import com.finance.util.DateUtil;
import com.finance.vo.RecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FinanceRecordServiceImpl implements FinanceRecordService {

}