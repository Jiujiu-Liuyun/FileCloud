package com.zhangyun.filecloud.server.database.service;

import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
    FileChangeRecord selectByPathAndDeviceId(String relativePath, String deviceId);
    List<FileChangeRecord> selectByDeviceId(String deviceId, Integer limit);
}
