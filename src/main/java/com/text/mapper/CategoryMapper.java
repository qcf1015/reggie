package com.text.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.text.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
