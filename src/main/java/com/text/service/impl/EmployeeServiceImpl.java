package com.text.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.text.entity.Employee;
import com.text.mapper.EmployeeMapper;
import com.text.service.EmployeeService;
import org.springframework.stereotype.Service;


@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
