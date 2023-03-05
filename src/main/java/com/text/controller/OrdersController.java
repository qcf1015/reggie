package com.text.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.text.common.BaseContext;
import com.text.common.R;
import com.text.dto.DishDto;
import com.text.dto.OrdersDto;
import com.text.entity.Category;
import com.text.entity.Dish;
import com.text.entity.OrderDetail;
import com.text.entity.Orders;
import com.text.service.OrderDetailService;
import com.text.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("用户下单：{}",orders);
        ordersService.submit(orders);
        return R.success("下单成功");
    }


    /**
     * 查询所有
     * @param orders
     * @return
     */
    @GetMapping("/list")
    public R<List<Orders>> list(Orders orders){
        log.info("查询所有：{}",orders);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getThreadLocal());
        queryWrapper.orderByAsc(Orders::getOrderTime);
        List<Orders> list = ordersService.list(queryWrapper);
        return R.success(list);
    }
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
//        分页构造器
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
//        构造条件
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,BaseContext.getThreadLocal());
        queryWrapper.orderByAsc(Orders::getOrderTime);
        ordersService.page(ordersPage,queryWrapper);
//        对象拷贝
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        List<Orders> records = ordersPage.getRecords();
        List<OrdersDto> list = records.stream().map((record)->{
            OrdersDto ordersDto = new OrdersDto();
            Long userId = ordersDto.getUserId();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId,userId);
            List<OrderDetail> list1 = orderDetailService.list(lambdaQueryWrapper);
            BeanUtils.copyProperties(record,ordersDto);
            ordersDto.setOrderDetails(list1);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(list);
        return R.success(ordersDtoPage);
    }
}
