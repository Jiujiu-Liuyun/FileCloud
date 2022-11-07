package com.zhangyun.filecloud.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyun.filecloud.server.entity.Device;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
public interface IDeviceService extends IService<Device> {
    boolean createDevice(String deviceId, String username, String deviceName, String rootPath);
    Device selectDeviceByDeviceId(String deviceId);
}
