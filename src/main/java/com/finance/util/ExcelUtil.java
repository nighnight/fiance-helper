package com.finance.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Excel 导出工具类
 * 依赖：
 * <dependency>
 *      <groupId>org.apache.poi</groupId>
 *      <artifactId>poi-ooxml</artifactId>
 *      <version>5.2.3</version>
 * </dependency>
 */
public class ExcelUtil {

    /**
     * 导出 Excel
     * @param response HttpServletResponse
     * @param fileName 文件名
     * @param sheetName sheet名称
     * @param headers Excel 表头数组
     * @param dataList 业务数据列表
     * @param fieldNames 数据对象中与表头对应的字段名数组 (顺序一致)
     * @param <T> 数据类型
     * @throws IOException
     */
    public static <T> void exportExcel(HttpServletResponse response, String fileName, String sheetName, String[] headers, List<T> dataList, String[] fieldNames) throws IOException {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // 创建标题行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (T data : dataList) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < fieldNames.length; i++) {
                Cell cell = row.createCell(i);
                try {
                    Field field = data.getClass().getDeclaredField(fieldNames[i]);
                    field.setAccessible(true); // 允许访问私有字段
                    Object value = field.get(data);
                    if (value instanceof String) {
                        cell.setCellValue((String) value);
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else if (value instanceof LocalDate) {
                        cell.setCellValue(DateUtil.formatDate((LocalDate) value));
                    } else if (value instanceof LocalDateTime) {
                        cell.setCellValue(((LocalDateTime) value).format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else if (value != null) {
                        cell.setCellValue(value.toString());
                    } else {
                        cell.setCellValue("");
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    cell.setCellValue("N/A"); // 字段不存在或访问失败
                }
            }
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8).replace("+", "%20"));
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}