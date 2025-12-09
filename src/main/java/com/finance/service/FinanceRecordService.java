package com.finance.service;

import com.finance.dto.FinanceRecordDTO;
import com.finance.vo.RecordVO;

import java.time.LocalDate;
import java.util.List;

public interface FinanceRecordService {
    void addRecord(Long userId, FinanceRecordDTO recordDTO);
    void updateRecord(Long userId, FinanceRecordDTO recordDTO);
    void deleteRecord(Long userId, Long recordId);
    RecordVO getRecordById(Long userId, Long recordId);
    List<RecordVO> getRecords(Long userId, LocalDate startDate, LocalDate endDate, Integer type, Long categoryId, Long accountId);
    // 上传凭证（单独方法，或者放入add/update Record中）
    void uploadVoucher(Long userId, Long recordId, String voucherUrl);
}