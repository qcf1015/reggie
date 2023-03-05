package com.text.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.text.entity.Orders;
import com.text.mapper.OrdersMapper;

public interface OrdersService extends IService<Orders> {

//    用户下单
    void submit(Orders orders);
}
