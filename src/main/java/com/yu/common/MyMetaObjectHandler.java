package com.yu.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 新增的时候自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        Long id = BaseContext.getCurrentId();
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", id);
        metaObject.setValue("createUser", id);
    }

    /**
     * 修改的时候自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        Long id = BaseContext.getCurrentId();
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", id);
    }
}
