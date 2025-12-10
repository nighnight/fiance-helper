package com.finance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分类传输对象")
public class CategoryDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryName;

    @Schema(description = "类型：1-收入，2-支出", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;

    @Schema(description = "排序字段（越小越靠前）")
    private Integer sort;
}