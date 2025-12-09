package com.finance.vo;

import lombok.Data;

@Data
public class CategoryVO {
    private Long id;
    private String categoryName;
    private Integer type; // 1-收入，2-支出
    private String typeName; // 收入/支出
    private Integer sort;
    private Integer isDefault;
}