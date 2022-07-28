package com.yu.common;

public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    //设置线程存储id
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    //获取线程存储的id
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
