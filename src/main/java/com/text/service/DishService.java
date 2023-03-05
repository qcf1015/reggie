package com.text.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.text.dto.DishDto;
import com.text.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

//    操作两张表,新增菜品同时插入对应的口味:dish,dish_flavor
    void saveDishAndFlavor(DishDto dishDto);

//      操作两张表，根据id查询相应的信息
    DishDto getDishAndFlavor(Long id);

//    操作两张表,新增菜品同时插入对应的口味:dish,dish_flavor
    void updateDishAndFlavor(DishDto dishDto);

    //根据传过来的id批量或者是单个的删除菜品，并判断是否是启售的
    public void deleteByIds(List<Long> ids);

    //菜品批量删除和单个删除，删除时用到deleteByIds方法删除菜品
    public boolean deleteInSetmeal(List<Long> ids);


}
