package com.finance.service;

import com.finance.dto.FinanceRecordDTO;
import com.finance.po.FinanceRecord;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface FinanceRecordService {
    List<FinanceRecord> getList(HttpSession session);
    void addRecord(FinanceRecordDTO dto, HttpSession session);
    FinanceRecord getById(Long id);
    void updateRecord(FinanceRecordDTO dto);
    void deleteRecord(Long id);
    ByteArrayInputStream exportRecords(HttpSession session) throws IOException;
    PageInfo<FinanceRecord> getList(int pageNum, int pageSize, HttpSession session);
}