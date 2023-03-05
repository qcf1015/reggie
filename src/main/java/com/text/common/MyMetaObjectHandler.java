package com.text.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 自定义源数据处理器
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
//获取id方法一：自动装配
//    @Autowired
//    private HttpServletRequest request;
    /**
     * 新增操作，自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充："+metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getThreadLocal());
        metaObject.setValue("updateUser", BaseContext.getThreadLocal());
//        metaObject.setValue("createUser", request.getSession().getAttribute("employee"));
//        metaObject.setValue("updateUser", request.getSession().getAttribute("employee"));
    }

    /**
     * 更新操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充："+metaObject.toString());
//        metaObject.setValue("updateUser",request.getSession().getAttribute("employee"));
        metaObject.setValue("updateUser",BaseContext.getThreadLocal());
        metaObject.setValue("updateTime",LocalDateTime.now());
    }
}
