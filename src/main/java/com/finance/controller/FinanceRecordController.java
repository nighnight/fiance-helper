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

    @Autowired
    private FinanceRecordService financeRecordService;
    @Autowired
    private HttpSession session;

    private Long getUserId() {
        LoginUserVO loginUser = (LoginUserVO) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException("用户未登录或会话已过期");
        }
        return loginUser.getId();
    }

    /**
     * 跳转收支记录列表页面
     * @param model Model
     * @return "record/list"
     */
    @GetMapping("/list")
    public String recordListPage(Model model) {
        // 默认查询当月记录
        LocalDate today = LocalDate.now();
        LocalDate startDate = DateUtil.getFirstDayOfMonth(today);
        LocalDate endDate = DateUtil.getLastDayOfMonth(today);

        model.addAttribute("startDate", DateUtil.formatDate(startDate));
        model.addAttribute("endDate", DateUtil.formatDate(endDate));
        return "record/list";
    }

    /**
     * 获取收支记录列表API (带筛选条件)
     * @param startDateStr 开始日期字符串
     * @param endDateStr 结束日期字符串
     * @param type 1-收入，2-支出 (可选)
     * @param categoryId 类别ID (可选)
     * @param accountId 账户ID (可选)
     * @return JSON结果
     */
    @GetMapping("/api/list")
    @ResponseBody
    public Result<List<RecordVO>> getRecords(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long accountId) {
        Long userId = getUserId();
        List<RecordVO> records = financeRecordService.getRecords(userId, startDate, endDate, type, categoryId, accountId);
        return ResultUtil.success(records);
    }

    /**
     * 跳转添加收支记录页面
     * @param model Model
     * @return "record/add"
     */
    @GetMapping("/add")
    public String addRecordPage(Model model) {
        FinanceRecordDTO recordDTO = new FinanceRecordDTO();
        recordDTO.setRecordDate(LocalDate.now()); // 默认当前日期
        model.addAttribute("recordDTO", recordDTO);
        return "record/add";
    }

    /**
     * 添加收支记录API
     * @param recordDTO 记录DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/add")
    @ResponseBody
    public Result<String> addRecord(@Valid @RequestBody FinanceRecordDTO recordDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Long userId = getUserId();
        financeRecordService.addRecord(userId, recordDTO);
        return ResultUtil.success("收支记录添加成功");
    }

    /**
     * 跳转收支记录详情/编辑页面
     * @param id 记录ID
     * @param model Model
     * @return "record/detail" 或 "record/edit" (这里使用detail页面同时进行编辑)
     */
    @GetMapping("/detail/{id}")
    public String recordDetailPage(@PathVariable Long id, Model model) {
        Long userId = getUserId();
        RecordVO recordVO = financeRecordService.getRecordById(userId, id);
        if (recordVO == null) {
            throw new BusinessException("收支记录不存在");
        }
        model.addAttribute("record", recordVO);
        return "record/detail";
    }

    /**
     * 更新收支记录API
     * @param id 记录ID
     * @param recordDTO 记录DTO
     * @param bindingResult 参数校验结果
     * @return JSON结果
     */
    @PostMapping("/api/update/{id}")
    @ResponseBody
    public Result<String> updateRecord(@PathVariable Long id, @Valid @RequestBody FinanceRecordDTO recordDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(400, bindingResult.getFieldError().getDefaultMessage());
        }
        Long userId = getUserId();
        recordDTO.setId(id);
        financeRecordService.updateRecord(userId, recordDTO);
        return ResultUtil.success("收支记录更新成功");
    }

    /**
     * 删除收支记录API
     * @param id 记录ID
     * @return JSON结果
     */
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public Result<String> deleteRecord(@PathVariable Long id) {
        Long userId = getUserId();
        financeRecordService.deleteRecord(userId, id);
        return ResultUtil.success("收支记录删除成功");
    }
}