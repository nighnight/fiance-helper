package com.finance.service.impl;

import com.finance.dto.FinanceRecordDTO;
import com.finance.exception.BusinessException;
import com.finance.mapper.AccountMapper;
import com.finance.mapper.SysUserMapper;
import com.finance.po.Account;
import com.finance.po.FinanceRecord;
import com.finance.po.SysUser;
import com.finance.service.FinanceRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class FinanceRecordServiceImplTest {

    @Autowired
    private FinanceRecordService recordService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private AccountMapper accountMapper;

    private MockHttpSession session;
    private Long testUserId;
    private Long testAccountId;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();

        // 创建测试用户
        SysUser user = new SysUser();
        user.setUsername("testuser");
        user.setNickname("测试用户");
        user.setPassword("encodedpassword");
        userMapper.insert(user);
        SysUser dbUser = userMapper.selectByUsername("testuser");
        testUserId = dbUser.getId();
        session.setAttribute("USER_SESSION", dbUser);

        // 创建测试账户，初始余额 1000
        Account account = new Account();
        account.setUserId(testUserId);
        account.setAccountName("测试账户");
        account.setAccountType("现金");
        account.setInitialBalance(new BigDecimal("1000"));
        account.setCurrentBalance(new BigDecimal("1000"));
        accountMapper.insert(account);
        List<Account> accounts = accountMapper.selectByUserId(testUserId);
        testAccountId = accounts.get(0).getId();
    }

    @Test
    @DisplayName("REC-01: 新增收入，余额增加")
    void addIncome() {
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(1); // 收入
        dto.setAmount(new BigDecimal("500"));
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());

        recordService.addRecord(dto, session);

        Account account = accountMapper.selectById(testAccountId);
        assertThat(account.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("1500"));
    }

    @Test
    @DisplayName("REC-02: 新增支出（余额充足），余额扣减")
    void addExpenseWithSufficientBalance() {
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(2); // 支出
        dto.setAmount(new BigDecimal("200"));
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());

        recordService.addRecord(dto, session);

        Account account = accountMapper.selectById(testAccountId);
        assertThat(account.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("800"));
    }

    @Test
    @DisplayName("REC-03: 新增支出（余额不足）应拦截")
    void addExpenseWithInsufficientBalanceShouldFail() {
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(2); // 支出
        dto.setAmount(new BigDecimal("1500")); // 超过余额 1000
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());

        assertThatThrownBy(() -> recordService.addRecord(dto, session))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("余额不足");

        // 验证余额未变
        Account account = accountMapper.selectById(testAccountId);
        assertThat(account.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("REC-04: 新增支出（余额刚好相等）")
    void addExpenseWithExactBalance() {
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(2); // 支出
        dto.setAmount(new BigDecimal("1000")); // 刚好等于余额
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());

        recordService.addRecord(dto, session);

        Account account = accountMapper.selectById(testAccountId);
        assertThat(account.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("0"));
    }

    @Test
    @DisplayName("REC-05: 删除收入记录，余额回退")
    void deleteIncomeRecord() {
        // 先新增一条收入
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(1); // 收入
        dto.setAmount(new BigDecimal("500"));
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());
        recordService.addRecord(dto, session);

        // 查询记录并删除
        List<FinanceRecord> records = recordService.getList(session);
        assertThat(records).hasSize(1);
        Long recordId = records.get(0).getId();

        recordService.deleteRecord(recordId);

        Account account = accountMapper.selectById(testAccountId);
        assertThat(account.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("REC-06: 删除支出记录，余额回退")
    void deleteExpenseRecord() {
        // 先新增一条支出
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(2); // 支出
        dto.setAmount(new BigDecimal("300"));
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());
        recordService.addRecord(dto, session);

        List<FinanceRecord> records = recordService.getList(session);
        assertThat(records).hasSize(1);
        Long recordId = records.get(0).getId();

        recordService.deleteRecord(recordId);

        Account account = accountMapper.selectById(testAccountId);
        assertThat(account.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("REC-07: 修改记录（支出改小），余额联动正确")
    void updateRecordReduceExpense() {
        // 先新增支出 500，余额变为 500
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(2);
        dto.setAmount(new BigDecimal("500"));
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());
        recordService.addRecord(dto, session);

        Long recordId = recordService.getList(session).get(0).getId();

        // 修改为支出 200，应回退 300
        FinanceRecordDTO updateDto = new FinanceRecordDTO();
        updateDto.setId(recordId);
        updateDto.setType(2);
        updateDto.setAmount(new BigDecimal("200"));
        updateDto.setAccountId(testAccountId);
        updateDto.setCategoryId(1L);
        updateDto.setRecordDate(LocalDate.now());
        recordService.updateRecord(updateDto);

        Account account = accountMapper.selectById(testAccountId);
        assertThat(account.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("800"));
    }

    @Test
    @DisplayName("REC-08: 根据ID查询记录")
    void getById() {
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(1);
        dto.setAmount(new BigDecimal("100"));
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());
        recordService.addRecord(dto, session);

        Long recordId = recordService.getList(session).get(0).getId();
        FinanceRecord record = recordService.getById(recordId);

        assertThat(record).isNotNull();
        assertThat(record.getAmount()).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    @DisplayName("REC-09: 导出Excel不为空")
    void exportRecords() throws IOException {
        // 先新增一条记录，确保有数据可导出
        FinanceRecordDTO dto = new FinanceRecordDTO();
        dto.setType(1);
        dto.setAmount(new BigDecimal("100"));
        dto.setAccountId(testAccountId);
        dto.setCategoryId(1L);
        dto.setRecordDate(LocalDate.now());
        recordService.addRecord(dto, session);

        java.io.ByteArrayInputStream in = recordService.exportRecords(session);
        assertThat(in).isNotNull();
        assertThat(in.available()).isGreaterThan(0);
    }

    @Test
    @DisplayName("REC-10: 分页查询记录")
    void getListWithPagination() {
        // 新增两条记录
        FinanceRecordDTO dto1 = new FinanceRecordDTO();
        dto1.setType(1);
        dto1.setAmount(new BigDecimal("100"));
        dto1.setAccountId(testAccountId);
        dto1.setCategoryId(1L);
        dto1.setRecordDate(LocalDate.now());
        recordService.addRecord(dto1, session);

        FinanceRecordDTO dto2 = new FinanceRecordDTO();
        dto2.setType(2);
        dto2.setAmount(new BigDecimal("50"));
        dto2.setAccountId(testAccountId);
        dto2.setCategoryId(1L);
        dto2.setRecordDate(LocalDate.now());
        recordService.addRecord(dto2, session);

        com.github.pagehelper.PageInfo<FinanceRecord> pageInfo = recordService.getList(1, 10, session);
        assertThat(pageInfo.getList()).hasSize(2);
        assertThat(pageInfo.getPageNum()).isEqualTo(1);
        assertThat(pageInfo.getPageSize()).isEqualTo(10);
    }
}
