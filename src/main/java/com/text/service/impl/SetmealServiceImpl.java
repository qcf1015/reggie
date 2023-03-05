package com.text.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.text.common.CustomException;
import com.text.common.R;
import com.text.dto.SetmealDto;
import com.text.entity.Setmeal;
import com.text.entity.SetmealDish;
import com.text.mapper.SetmealMapper;
import com.text.service.SetmealDishService;
import com.text.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 保存套餐信息
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveDishAndSetmeal(SetmealDto setmealDto) {
//        保存套餐的基本信息
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List list = new ArrayList();
        for (SetmealDish setmealDish : setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId());
            list.add(setmealDish);
        }
//        setmealDishes.stream().map((item) -> {
//            item.setSetmealId(setmealDto.getId());
//            return item;
//        }).collect(Collectors.toList());

//        保存菜品和套餐的关联的基本信息
        setmealDishService.saveBatch(list);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @Override
    @Transactional
    public void removeAndDish(List<Long> ids) {
//        判断是否起售
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count > 0){
            throw new CustomException("套餐正在售卖，不能删除");
        }
        this.removeByIds(ids);
//        删除关系表的数据：setmealdish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

}
