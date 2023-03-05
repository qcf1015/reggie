package com.text.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.text.common.BaseContext;
import com.text.common.R;
import com.text.entity.ShoppingCart;
import com.text.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车：{}",shoppingCart);
//        设置用户id
        shoppingCart.setUserId(BaseContext.getThreadLocal());
//        查询当前菜品或者套餐是否存在
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
//        判断是套餐还是菜品
        if (dishId != null){
//            菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        } else {
//            套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
//        使用getone是为了得到返回实体对象
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one != null){
            //        如果存在，在原来的数量加
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        } else {
            //        如果不存在，则默认
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 减少购物
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("减少购物");
//        得到id
        shoppingCart.setUserId(BaseContext.getThreadLocal());
//        查询当前菜品或者套餐是否存在
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
//        判断是套餐还是菜品
        if (dishId != null){
//            菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        } else {
//            套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
//        使用getone是为了得到返回实体对象
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        if (one != null){
            //        如果存在，在原来的数量减
            Integer number = one.getNumber();
            if (number > 0) {
                one.setNumber(number - 1);
                shoppingCartService.updateById(one);
            }
            if (number == 0){
                shoppingCartService.removeById(one);
            }
        } else {
            //        如果不存在，则默认
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getThreadLocal());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getThreadLocal());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }
}
