package com.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class CategoryDTO {
    private Long id; // 用于更新时
    @NotBlank(message = "类别名称不能为空")
    private String categoryName;
    @NotNull(message = "类别类型不能为空")
    @Min(value = 1, message = "类别类型无效")
    @Max(value = 2, message = "类别类型无效")
    private Integer type; // 1-收入，2-支出
    private Integer sort;
}