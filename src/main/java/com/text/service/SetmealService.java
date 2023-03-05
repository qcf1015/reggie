package com.text.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.text.dto.SetmealDto;
import com.text.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

//    保存信息：包括dish和setmeal
    void saveDishAndSetmeal(SetmealDto setmealDto);

//    删除套餐
    void removeAndDish(List<Long> ids);
}
