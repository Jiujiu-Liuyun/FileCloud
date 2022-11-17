package com.zhangyun.filecloud.server.database.service.impl;

import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.mapper.FileChangeRecordMapper;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zy
 * @since 2022-11-17
 */
@Service
public class FileChangeRecordServiceImpl extends ServiceImpl<FileChangeRecordMapper, FileChangeRecord> implements FileChangeRecordService {
    @Autowired
    private FileChangeRecordMapper fileChangeRecordMapper;

    @Override
    public boolean insertOne(FileChangeRecord fileChangeRecord) {
        int insert = fileChangeRecordMapper.insert(fileChangeRecord);
        return insert > 0;
    }

    @Override
    public boolean deleteById(int id) {
        int delete = fileChangeRecordMapper.deleteById(id);
        return delete > 0;
    }
}
