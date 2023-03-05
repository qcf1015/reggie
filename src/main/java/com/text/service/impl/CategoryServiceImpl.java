package com.text.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.text.common.CustomException;
import com.text.entity.Category;
import com.text.entity.Dish;
import com.text.entity.Setmeal;
import com.text.mapper.CategoryMapper;
import com.text.service.CategoryService;
import com.text.service.DishService;
import com.text.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除之前需要进行判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {
//        查询当前分类是否关联菜品，如果关联，抛出业务异常
        LambdaQueryWrapper<Dish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Dish::getCategoryId,ids);
        int count1 = dishService.count(queryWrapper1);
        if (count1>0){
            throw new CustomException("当前分类关联了菜品，不能删除");
        }
//        查询当前分类是否关联套餐，如果关联，抛出业务异常
        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<>();
        queryWrapper2.eq(Setmeal::getCategoryId,ids);
        int count2 = setmealService.count(queryWrapper2);
        if (count2>0){
            throw new CustomException("当前分类关联了套餐，不能删除");
        }
//        正常删除
        super.removeById(ids);
    }
}
