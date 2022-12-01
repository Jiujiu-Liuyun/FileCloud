package com.zhangyun.filecloud.server.database.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.FileTypeEnum;
import com.zhangyun.filecloud.common.enums.OperationTypeEnum;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.mapper.FileChangeRecordMapper;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    /**
     * 插入一条FCR记录，如果存相同的记录，则先删除
     * @param fileChangeRecord
     * @return
     */
    @Override
    public boolean insertOne(FileChangeRecord fileChangeRecord) {
        // 查询是否存在 deviceId + relativePath + fileType
        List<FileChangeRecord> fileChangeRecords = selectBatchByPathDeviceType(fileChangeRecord.getRelativePath(), fileChangeRecord.getDeviceId(), fileChangeRecord.getFileType());
        List<Integer> ids = fileChangeRecords.stream().mapToInt(FileChangeRecord::getId).boxed().collect(Collectors.toList());
        // 如果存在则删除
        if (!ids.isEmpty()) {
            fileChangeRecordMapper.deleteBatchIds(ids);
        }
        // 插入新数据
        int insert = fileChangeRecordMapper.insert(fileChangeRecord);
        return insert > 0;
    }

    /**
     * 更新传输位置
     * @param startPos
     * @param relativePath
     * @param deviceId
     */
    @Override
    public void updateStartPosById(Long startPos, Integer id) {
        FileChangeRecord fileChangeRecord =  new FileChangeRecord();
        fileChangeRecord.setId(id);
        fileChangeRecord.setStartPos(startPos);
        fileChangeRecordMapper.updateById(fileChangeRecord);
    }

    /**
     * 批量插入数据
     * @param fileChangeRecords
     */
    @Override
    public void insertBatch(List<FileChangeRecord> fileChangeRecords) {
        for (FileChangeRecord fileChangeRecord : fileChangeRecords) {
            if (!insertOne(fileChangeRecord)) {
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

    private List<FileChangeRecord> selectBatchByPathDeviceType(String relativePath, String deviceId, Integer fileType) {
        QueryWrapper<FileChangeRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("relative_path", relativePath);
        wrapper.eq("device_id", deviceId);
        wrapper.eq("file_type", fileType);
        return fileChangeRecordMapper.selectList(wrapper);
    }

    /**
     * 获取该设备的下一个FCR
     * @param deviceId
     * @return
     */
    @Override
    public FileChangeRecord selectNextFCR(String deviceId) {
        // 根据deviceId获取FCR
        Page<FileChangeRecord> page = new Page<>(1, 1);
        QueryWrapper<FileChangeRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("device_id", deviceId);
        List<FileChangeRecord> fileChangeRecords = fileChangeRecordMapper.selectPage(page, wrapper).getRecords();
        if (fileChangeRecords.isEmpty()) {
            return null;
        }
        FileChangeRecord fileChangeRecord = fileChangeRecords.get(0);
        // 根据FCR查找最新FCR
        return selectFCRByRelativePathAndDeviceId(fileChangeRecord.getRelativePath(), fileChangeRecord.getDeviceId(),
                fileChangeRecord.getFileType());
    }

    /**
     * 根据deviceId relativePath fileType查询最新更改的记录，其他同一记录删除
     * @param relativePath
     * @param deviceId
     * @return
     */
    @Override
    public FileChangeRecord selectFCRByRelativePathAndDeviceId(String relativePath, String deviceId, Integer fileType) {
        List<FileChangeRecord> fileChangeRecords = selectBatchByPathDeviceType(relativePath, deviceId, fileType);
        if (fileChangeRecords.isEmpty()) {
            return null;
        }
        if (fileChangeRecords.size() == 1) {
            return fileChangeRecords.get(0);
        }
        fileChangeRecords.sort(Comparator.comparingLong(fcr -> fcr.getModifiedTime().getTime()));
        // 最新的更改
        FileChangeRecord fileChangeRecord = fileChangeRecords.get(fileChangeRecords.size() - 1);
        // 删除其他
        List<Integer> ids = fileChangeRecords.stream()
                .filter(fileChangeRecord1 -> ObjectUtil.notEqual(fileChangeRecord.getId(), fileChangeRecord1.getId()))
                .mapToInt(FileChangeRecord::getId).boxed().collect(Collectors.toList());
        deleteBatchIds(ids);
        return fileChangeRecord;
    }

    /**
     * convert FileTrfBO===》FileChangeRecord
     * @param fileTrfBO
     * @return
     */
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
        fileChangeRecord.setId(fileTrfBO.getId());
        return fileChangeRecord;
    }

    /**
     * convert FileChangeRecord ===》FileTrfBO
     * @param fileChangeRecord
     * @return
     */
    @Override
    public FileTrfBO convertFileChangeRecordToFTBO(FileChangeRecord fileChangeRecord) {
        if (fileChangeRecord == null) {
            return null;
        }
        FileTrfBO fileTrfBO = new FileTrfBO();
        fileTrfBO.setRelativePath(fileChangeRecord.getRelativePath());
        fileTrfBO.setFileTypeEnum(FileTypeEnum.getTypeName(fileChangeRecord.getFileType()));
        fileTrfBO.setOperationTypeEnum(OperationTypeEnum.getTypeName(fileChangeRecord.getOperationType()));
        fileTrfBO.setDeviceId(fileChangeRecord.getDeviceId());
        fileTrfBO.setTransferModeEnum(TransferModeEnum.getTypeName(fileChangeRecord.getTransferMode()));
        fileTrfBO.setStartPos(fileChangeRecord.getStartPos());
        fileTrfBO.setStatusEnum(StatusEnum.getTypeName(fileChangeRecord.getStatus()));
        fileTrfBO.setMaxReadLength(fileChangeRecord.getMaxReadLength());
        fileTrfBO.setId(fileChangeRecord.getId());
        return fileTrfBO;
    }
}
