package com.text.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.text.entity.Category;
import com.text.mapper.CategoryMapper;

public interface CategoryService extends IService<Category> {

    void remove(Long id);
}
