package com.zhangyun.filecloud.server.database.service;

import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyun.filecloud.server.database.mapper.FileChangeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zy
 * @since 2022-11-17
 */
public interface FileChangeRecordService extends IService<FileChangeRecord> {
    boolean insertOne(FileChangeRecord fileChangeRecord);
    boolean deleteById(int id);
}
