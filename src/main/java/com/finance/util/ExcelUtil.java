package com.finance.util;

import com.finance.po.FinanceRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {

    public static ByteArrayInputStream recordsToExcel(List<FinanceRecord> records) throws IOException {
        String[] HEADERS = {"日期", "类型", "分类", "账户", "金额", "备注"};
        String SHEET = "收支明细";

        try (
                // 1. 创建一个新的 Excel 工作簿
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
        ) {
            // 2. 创建一个工作表
            Sheet sheet = workbook.createSheet(SHEET);

            // 3. 创建表头行
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERS.length; col++) {
                headerRow.createCell(col).setCellValue(HEADERS[col]);
            }

            // 4. 填充数据行
            int rowIdx = 1;
            for (FinanceRecord record : records) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(record.getRecordDate().toString());
                row.createCell(1).setCellValue(record.getType() == 1 ? "收入" : "支出");
                row.createCell(2).setCellValue(record.getCategoryName());
                row.createCell(3).setCellValue(record.getAccountName());
                row.createCell(4).setCellValue(record.getAmount().doubleValue());
                row.createCell(5).setCellValue(record.getRemark());
            }
            
            // 将 Excel 内容写入内存中的输出流
            workbook.write(out);
            
            // 将输出流转换为输入流返回
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}