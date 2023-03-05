package com.text.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.text.entity.DishFlavor;
import com.text.mapper.DishFlavorMapper;
import com.text.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
