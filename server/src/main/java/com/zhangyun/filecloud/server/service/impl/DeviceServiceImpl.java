package com.zhangyun.filecloud.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyun.filecloud.server.entity.Device;
import com.zhangyun.filecloud.server.mapper.DeviceMapper;
import com.zhangyun.filecloud.server.service.IDeviceService;
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
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

}
