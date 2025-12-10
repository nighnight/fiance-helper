package com.finance.controller;

import com.finance.dto.FinanceRecordDTO;
import com.finance.po.FinanceRecord;
import com.finance.service.FinanceRecordService;
import com.finance.util.Result;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/record")
@Tag(name = "收支记录管理")
public class FinanceRecordController {

    @Autowired
    private FinanceRecordService recordService;

    // === 页面跳转 ===

    @Operation(summary = "页面：收支明细列表", hidden = true)
    @GetMapping("/index")
    public String indexPage() {
        return "record/list";
    }

    @Operation(summary = "页面：记一笔", hidden = true)
    @GetMapping("/toAdd")
    public String addPage() {
        return "record/add";
    }

    @Operation(summary = "页面：编辑记录", hidden = true)
    @GetMapping("/toEdit")
    public String editPage() {
        return "record/edit";
    }

    // === 数据接口 ===

    @Operation(summary = "查询记录列表 (分页)")
    @GetMapping("/list")
    @ResponseBody
    public Result<PageInfo<FinanceRecord>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {

        PageInfo<FinanceRecord> pageInfo = recordService.getList(pageNum, pageSize, session);
        return Result.success(pageInfo);
    }

    @Operation(summary = "新增记录")
    @PostMapping
    @ResponseBody
    public Result add(@RequestBody FinanceRecordDTO dto, HttpSession session) {
        try {
            recordService.addRecord(dto, session);
            return Result.success();
        } catch (Exception e) {
            return Result.error("记账失败：" + e.getMessage());
        }
    }

    @Operation(summary = "根据ID查询")
    @GetMapping("/{id}")
    @ResponseBody
    public Result<FinanceRecord> getById(@PathVariable Long id) {
        return Result.success(recordService.getById(id));
    }

    @Operation(summary = "修改记录")
    @PutMapping
    @ResponseBody
    public Result update(@RequestBody FinanceRecordDTO dto) {
        try {
            recordService.updateRecord(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error("修改失败：" + e.getMessage());
        }
    }

    @Operation(summary = "删除记录")
    @DeleteMapping("/{id}")
    @ResponseBody
    public Result delete(@PathVariable Long id) {
        recordService.deleteRecord(id);
        return Result.success();
    }

    @Operation(summary = "导出收支记录为Excel")
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> export(HttpSession session) throws IOException {

        // 1. 从 Service 获取 Excel 文件流
        ByteArrayInputStream in = recordService.exportRecords(session);

        // 2. 设置 HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        String filename = "finance-records-" + LocalDate.now() + ".xlsx";

        // a. 告诉浏览器这是一个 Excel 文件
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        // b. 设置文件名，并让浏览器弹出下载框
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        // 3. 返回 ResponseEntity
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(in));
    }
}