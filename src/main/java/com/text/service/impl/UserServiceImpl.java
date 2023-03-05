package com.text.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.text.entity.User;
import com.text.mapper.UserMapper;
import com.text.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
