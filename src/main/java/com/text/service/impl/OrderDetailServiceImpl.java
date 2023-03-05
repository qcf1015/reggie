package com.text.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.text.entity.OrderDetail;
import com.text.mapper.OrderDetailMapper;
import com.text.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
