package com.text.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.text.entity.ShoppingCart;
import com.text.mapper.ShoppingCartMapper;
import com.text.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
