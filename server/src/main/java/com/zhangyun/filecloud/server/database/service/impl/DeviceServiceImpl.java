package com.zhangyun.filecloud.server.database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyun.filecloud.server.database.entity.Device;
import com.zhangyun.filecloud.server.database.entity.User;
import com.zhangyun.filecloud.server.database.mapper.DeviceMapper;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.database.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangyun
 * @since 2022-11-02
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private UserService userService;

    @Override
    public boolean createDevice(String deviceId, String username, String deviceName, String rootPath) {
        Integer userId = userService.getIdByUsername(username);
        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setUserId(userId);
        device.setDeviceName(deviceName);
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

    @Override
    public boolean authDevice(String deviceId, String username) {
        Device device = selectDeviceByDeviceId(deviceId);
        if (device == null) {
            return false;
        }
        User user = userService.getById(device.getUserId());
        if (user == null) {
            return false;
        }
        return user.getUsername().equals(username);
    }

    @Override
    public List<Device> selectDevicesByUsername(String username) {
        Integer userId = userService.getIdByUsername(username);
        HashMap<String, Object> map = new HashMap<>();
        map.put("user_id", userId);
        return deviceMapper.selectByMap(map);
    }

    @Override
    public List<String> selectDeviceIdsByUsername(String username) {
        List<Device> devices = selectDevicesByUsername(username);
        List<String> deviceIds = new ArrayList<>();
        for (Device device : devices) {
            deviceIds.add(device.getDeviceId());
        }
        return deviceIds;
    }

    @Override
    public String getDeviceNameByDeviceId(String deviceId) {
        Device device = selectDeviceByDeviceId(deviceId);
        if (device != null) {
            return device.getDeviceName();
        }
        return null;
    }
}
