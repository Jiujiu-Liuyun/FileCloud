package com.zhangyun.filecloud.server.database.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangyun.filecloud.server.database.entity.Device;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
public interface DeviceService extends IService<Device> {
    boolean createDevice(String deviceId, String username);
    Device selectDeviceByDeviceId(String deviceId);
    boolean authDevice(String deviceId, String username);
    List<Device> selectDevicesByUsername(String username);
    List<String> selectDeviceIdsByUsername(String username);
}
