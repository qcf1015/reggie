package com.text.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.text.common.R;
import com.text.dto.DishDto;
import com.text.entity.Category;
import com.text.entity.Dish;
import com.text.entity.DishFlavor;
import com.text.service.CategoryService;
import com.text.service.DishFlavorService;
import com.text.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        boolean deleteInSetmeal = dishService.deleteInSetmeal(ids);
        if(deleteInSetmeal){
            return R.success("菜品删除成功");
        }
        else{
            return R.error("删除的菜品中有关联在售套餐,删除失败！");
        }
    }

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveDishAndFlavor(dishDto);
        return R.success("新增成功");
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateDishAndFlavor(dishDto);

//        redis优化：清理所有菜品的缓存
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

//        redis优化：清理具体的菜品缓存
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("修改成功");
    }

    /**
     * 根据id查询菜品信息和口味
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishAndFlavor = dishService.getDishAndFlavor(id);
        return R.success(dishAndFlavor);
    }

    /**
     * 菜品分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
//        分页构造器
        Page<Dish> page1 = new Page<>(page,pageSize);
        Page<DishDto> page2 = new Page<>();
//        条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName,name);
//        进行排序
        queryWrapper.orderByDesc(Dish::getSort);
        dishService.page(page1,queryWrapper);
//        对象拷贝
        BeanUtils.copyProperties(page1,page2,"records");
        List<Dish> records = page1.getRecords();
        List<DishDto> list = records.stream().map((record)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            Long category = dishDto.getCategoryId();
            Category byId = categoryService.getById(category);
            if (byId != null){
                String byIdName = byId.getName();
                dishDto.setCategoryName(byIdName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        page2.setRecords(list);
        return R.success(page2);
    }

    /**
     * 根据条件查询数据
     * @param dish
     * @return
     */
    /*
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }
    */

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
//        redis优化：
        List<DishDto> dishDtoList = null;
//        构建key
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
//        先从redis获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null){
            //        如果存在，直接缓存
            return R.success(dishDtoList);
        }
//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
//        拷贝
        dishDtoList= list.stream().map((record)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);
            Long category = dishDto.getCategoryId();
            Category byId = categoryService.getById(category);
            if (byId != null){
                String byIdName = byId.getName();
                dishDto.setCategoryName(byIdName);
            }
//            追加功能
            Long id = record.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> list1 = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(list1);
            return dishDto;
        }).collect(Collectors.toList());
        //        如果不存在，查询数据库，将菜品缓存到redis
        redisTemplate.opsForValue().set(key, dishDtoList);
        return R.success(dishDtoList);
    }

}
