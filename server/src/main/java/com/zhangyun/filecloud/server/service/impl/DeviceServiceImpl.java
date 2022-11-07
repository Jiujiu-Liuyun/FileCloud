package com.zhangyun.filecloud.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.server.entity.Device;
import com.zhangyun.filecloud.server.mapper.DeviceMapper;
import com.zhangyun.filecloud.server.service.IDeviceService;
import com.zhangyun.filecloud.server.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

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
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private IUserService userService;

    @Override
    public boolean createDevice(String deviceId, String username, String deviceName, String rootPath) {
        Integer userId = userService.getIdByUsername(username);
        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setDeviceName(deviceName);
        device.setUserId(userId);
        device.setRootPath(rootPath);
        int insert = deviceMapper.insert(device);
        return insert > 0;
    }

    @Override
    public Device selectDeviceByDeviceId(String deviceId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("device_id", deviceId);
        List<Device> devices = deviceMapper.selectByMap(map);
        if (devices == null || devices.isEmpty()) {
            return null;
        }
        return devices.get(0);
    }
}
