package com.finance.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DateUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 格式化 LocalDate 为 "yyyy-MM-dd" 字符串
     * @param date 日期
     * @return 格式化后的字符串
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * 解析 "yyyy-MM-dd" 字符串为 LocalDate
     * @param dateString 日期字符串
     * @return 解析后的日期
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    /**
     * 格式化 LocalDate 为 "yyyy-MM" 字符串
     * @param date 日期
     * @return 格式化后的月份字符串
     */
    public static String formatYearMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return YearMonth.from(date).format(YEAR_MONTH_FORMATTER);
    }

    /**
     * 解析 "yyyy-MM" 字符串为 YearMonth
     * @param yearMonthString 月份字符串
     * @return 解析后的月份
     */
    public static YearMonth parseYearMonth(String yearMonthString) {
        if (yearMonthString == null || yearMonthString.isEmpty()) {
            return null;
        }
        return YearMonth.parse(yearMonthString, YEAR_MONTH_FORMATTER);
    }

    /**
     * 获取两个日期之间的所有月份（包含起始月和结束月）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 包含所有月份的 YearMonth 列表
     */
    public static List<YearMonth> getYearMonthsBetween(LocalDate startDate, LocalDate endDate) {
        List<YearMonth> yearMonths = new ArrayList<>();
        YearMonth current = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        while (!current.isAfter(end)) {
            yearMonths.add(current);
            current = current.plusMonths(1);
        }
        return yearMonths;
    }

    /**
     * 获取指定日期所在月的第一天
     * @param date 指定日期
     * @return 该月的第一天
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return YearMonth.from(date).atDay(1);
    }

    /**
     * 获取指定日期所在月的最后一天
     * @param date 指定日期
     * @return 该月的最后一天
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return YearMonth.from(date).atEndOfMonth();
    }
}