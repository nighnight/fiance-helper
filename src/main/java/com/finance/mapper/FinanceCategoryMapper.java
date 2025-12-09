package com.finance.mapper;

import com.finance.po.FinanceCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FinanceCategoryMapper {
    int insert(FinanceCategory record);

    FinanceCategory selectByPrimaryKey(@Param("id") Long id);

    // 查询用户自定义类别和系统默认类别
    List<FinanceCategory> selectByUserIdAndType(@Param("userId") Long userId, @Param("type") Integer type);

    // 查询所有默认类别
    List<FinanceCategory> selectDefaultCategoriesByType(@Param("type") Integer type);

    // 检查用户是否已存在同名同类型类别
    FinanceCategory selectByUserIdCategoryNameAndType(@Param("userId") Long userId, @Param("categoryName") String categoryName, @Param("type") Integer type);

    int updateByPrimaryKeySelective(FinanceCategory record);

    int deleteByPrimaryKey(@Param("id") Long id, @Param("userId") Long userId); // 用户不能删除系统默认类别

    // 获取类别名称
    String selectCategoryNameById(@Param("id") Long id);
}