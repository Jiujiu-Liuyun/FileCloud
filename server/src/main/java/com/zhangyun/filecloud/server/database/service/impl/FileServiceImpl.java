package com.zhangyun.filecloud.server.database.service.impl;

import com.zhangyun.filecloud.server.database.entity.File;
import com.zhangyun.filecloud.server.database.mapper.FileMapper;
import com.zhangyun.filecloud.server.database.service.FileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zy
 * @since 2022-12-02
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

}
