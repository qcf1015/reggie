package com.text.common;


/**
 * 基于thread local封装工具类，用于保存用户的id
 * 获取id方法二：用线程threadlocal：同一线程，id不变
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setThreadLocal(Long id){
        threadLocal.set(id);
    }
    public static Long getThreadLocal(){
        return threadLocal.get();
    }
}
