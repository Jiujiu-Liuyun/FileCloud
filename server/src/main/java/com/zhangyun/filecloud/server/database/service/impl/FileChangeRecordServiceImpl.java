package com.zhangyun.filecloud.server.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.mapper.FileChangeRecordMapper;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zy
 * @since 2022-11-17
 */
@Service
@Slf4j
public class FileChangeRecordServiceImpl extends ServiceImpl<FileChangeRecordMapper, FileChangeRecord> implements FileChangeRecordService {
    @Autowired
    private FileChangeRecordMapper fileChangeRecordMapper;

    @Override
    public boolean deleteSameAndInsertOne(FileChangeRecord fileChangeRecord) {
        // 查询是否存在 deviceId + relativePath
        List<Integer> ids = selectIdsByPathAndDeviceId(fileChangeRecord.getRelativePath(), fileChangeRecord.getDeviceId());
        // 如果存在则删除
        if (ids != null && !ids.isEmpty()) {
            fileChangeRecordMapper.deleteBatchIds(ids);
        }
        // 插入新数据
        int insert = fileChangeRecordMapper.insert(fileChangeRecord);
        return insert > 0;
    }

    @Override
    public void updateStartPosById(Long startPos, Integer id) {
        FileChangeRecord fileChangeRecord = new FileChangeRecord();
        fileChangeRecord.setStartPos(startPos);
        fileChangeRecord.setId(id);
        fileChangeRecordMapper.updateById(fileChangeRecord);
    }

    @Override
    public void insertBatch(List<FileChangeRecord> fileChangeRecords) {
        for (FileChangeRecord fileChangeRecord : fileChangeRecords) {
            if (!deleteSameAndInsertOne(fileChangeRecord)) {
                log.info("insert fail: {}", fileChangeRecord);
            }
        }
    }

    @Override
    public boolean deleteById(int id) {
        int delete = fileChangeRecordMapper.deleteById(id);
        return delete > 0;
    }

    @Override
    public void deleteBatchIds(List<Integer> ids) {
        fileChangeRecordMapper.deleteBatchIds(ids);
    }

    @Override
    public List<Integer> selectIdsByPathAndDeviceId(String relativePath, String deviceId) {
        QueryWrapper<FileChangeRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("relative_path", relativePath);
        wrapper.eq("device_id", deviceId);
        List<FileChangeRecord> fileChangeRecords = fileChangeRecordMapper.selectList(wrapper);
        return fileChangeRecords.stream().mapToInt(FileChangeRecord::getId).boxed().collect(Collectors.toList());
    }

    @Override
    public List<FileChangeRecord> selectByDeviceId(String deviceId, Integer limit) {
        Page<FileChangeRecord> page = new Page<>(1, limit);
        QueryWrapper<FileChangeRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("device_id", deviceId);
        Page<FileChangeRecord> fileChangeRecordPage = fileChangeRecordMapper.selectPage(page, wrapper);
        return fileChangeRecordPage.getRecords();
    }

    @Override
    public FileChangeRecord convertFTBOtoFileChangeRecord(FileTrfBO fileTrfBO) {
        FileChangeRecord fileChangeRecord = new FileChangeRecord();
        fileChangeRecord.setRelativePath(fileTrfBO.getRelativePath());
        fileChangeRecord.setFileType(fileTrfBO.getFileTypeEnum().getCode());
        fileChangeRecord.setOperationType(fileTrfBO.getOperationTypeEnum().getCode());
        fileChangeRecord.setDeviceId(fileTrfBO.getDeviceId());
        fileChangeRecord.setTransferMode(fileTrfBO.getTransferModeEnum().getCode());
        fileChangeRecord.setStartPos(fileTrfBO.getStartPos());
        fileChangeRecord.setStatus(fileTrfBO.getStatusEnum().getCode());
        fileChangeRecord.setMaxReadLength(fileTrfBO.getMaxReadLength());
        return fileChangeRecord;
    }
}
