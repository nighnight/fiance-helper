package com.finance.mapper;

import com.finance.po.FinanceCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface FinanceCategoryMapper {
    // 查询某用户可见的所有分类（系统默认 + 用户自定义）
    List<FinanceCategory> selectByUserId(@Param("userId") Long userId);

    // 插入
    void insert(FinanceCategory category);

    // 根据ID查询
    FinanceCategory selectById(Long id);

    // 更新
    void update(FinanceCategory category);

    // 删除（只能删除用户自定义的）
    void deleteById(Long id);
}