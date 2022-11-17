package com.zhangyun.filecloud.server.database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyun.filecloud.server.database.entity.File;
import com.zhangyun.filecloud.server.database.mapper.FileMapper;
import com.zhangyun.filecloud.server.database.service.FileService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

}
