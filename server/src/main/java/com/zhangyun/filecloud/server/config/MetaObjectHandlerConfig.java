package com.zhangyun.filecloud.server.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/2 17:21
 * @since: 1.0
 */
@Slf4j
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {
    @Override
    @TraceLog
    public void insertFill(MetaObject metaObject) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        this.setFieldValByName("createTime", timestamp, metaObject);
        this.setFieldValByName("modifiedTime", timestamp, metaObject);
        log.info("insert {}, current: {}", metaObject, timestamp);
    }

    @Override
    @TraceLog
    public void updateFill(MetaObject metaObject) {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        this.setFieldValByName("modifiedTime", timestamp, metaObject);
    }
}
