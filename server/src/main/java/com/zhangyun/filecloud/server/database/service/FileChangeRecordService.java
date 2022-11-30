package com.zhangyun.filecloud.server.database.service;

import com.zhangyun.filecloud.common.entity.FileTrfBO;
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

    void updateStartPosById(Long startPos, Integer id);

    void insertBatch(List<FileChangeRecord> fileChangeRecords);

    boolean deleteById(int id);

    void deleteBatchIds(List<Integer> ids);

    FileChangeRecord selectNextFCR(String deviceId);

    FileChangeRecord selectFCRByRelativePathAndDeviceId(String relativePath, String deviceId, Integer fileType);

    FileChangeRecord convertFTBOtoFileChangeRecord(FileTrfBO fileTrfBO);

    FileTrfBO convertFileChangeRecordToFTBO(FileChangeRecord fileChangeRecord);
}
