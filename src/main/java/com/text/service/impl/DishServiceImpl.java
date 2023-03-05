package com.text.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.text.common.CustomException;
import com.text.dto.DishDto;
import com.text.entity.Dish;
import com.text.entity.DishFlavor;
import com.text.entity.Setmeal;
import com.text.entity.SetmealDish;
import com.text.mapper.DishMapper;
import com.text.service.DishFlavorService;
import com.text.service.DishService;
import com.text.service.SetmealDishService;
import com.text.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增菜品,同时保存口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveDishAndFlavor(DishDto dishDto) {
//        保存菜品的基本信息到菜品表
        this.save(dishDto);
//        得到菜品id
        Long dishId = dishDto.getId();
//        得到菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach((item)->{
            item.setDishId(dishId);
        });
//        保存菜品口味到口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和口味
     * @param id
     */
    @Override
    public DishDto getDishAndFlavor(Long id) {
//        查询菜品信息
        Dish dish = this.getById(id);
//        查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
//        传递信息
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(list);
        return dishDto;
    }

    /**
     * 跟新菜品
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateDishAndFlavor(DishDto dishDto) {
//        更新菜品
        this.updateById(dishDto);
//        更新口味,先清理flavor的delete
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
//        然后添加flavor的save
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach((item)->{
            item.setDishId(dishDto.getId());
        });
//        保存菜品口味到口味表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 批量删除
     * @param ids
     */
    @Transactional
    public void deleteByIds(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //先查询该菜品是否在售卖，如果是则抛出业务异常
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> list = this.list(queryWrapper);
        for (Dish dish : list) {
            Integer status = dish.getStatus();
            //如果不是在售卖,则可以删除
            if (status == 0){
                this.removeById(dish.getId());
            }else {
                //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
                throw new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
            }
        }
    }

    @Transactional
    public boolean deleteInSetmeal(List<Long> ids) {
        boolean flag=true;

        //1.根据菜品id在stemeal_dish表中查出哪些套餐包含该菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        List<SetmealDish> SetmealDishList = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //2.如果菜品没有关联套餐，直接删除就行  其实下面这个逻辑可以抽离出来，这里我就不抽离了
        if (SetmealDishList.size() == 0){
            //这个deleteByIds中已经做了菜品启售不能删除的判断力
            this.deleteByIds(ids);
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(DishFlavor::getDishId,ids);
            dishFlavorService.remove(queryWrapper);
            return flag;
        }

        //3.如果菜品有关联套餐，并且该套餐正在售卖，那么不能删除
        //3.1得到与删除菜品关联的套餐id
        ArrayList<Long> Setmeal_idList = new ArrayList<>();
        for (SetmealDish setmealDish : SetmealDishList) {
            Long setmealId = setmealDish.getSetmealId();
            Setmeal_idList.add(setmealId);
        }
        //3.2查询出与删除菜品相关联的套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,Setmeal_idList);
        List<Setmeal> setmealList = setmealService.list(setmealLambdaQueryWrapper);
        //3.3对拿到的所有套餐进行遍历，然后拿到套餐的售卖状态，如果有套餐正在售卖那么删除失败
        for (Setmeal setmeal : setmealList) {
            Integer status = setmeal.getStatus();
            if (status == 1){
                flag=false;
            }
        }

        //3.4要删除的菜品关联的套餐没有在售，可以删除
        //3.5这下面的代码并不一定会执行,因为如果前面的for循环中出现status == 1,那么下面的代码就不会再执行
        this.deleteByIds(ids);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);

        return flag;
    }
}
