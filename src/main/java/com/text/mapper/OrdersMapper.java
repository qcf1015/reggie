package com.text.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.text.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
