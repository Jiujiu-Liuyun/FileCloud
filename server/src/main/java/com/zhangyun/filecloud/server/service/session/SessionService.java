package com.zhangyun.filecloud.server.service.session;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description: 管理连接
 *
 * @author: zhangyun
 * @date: 2022/11/8 16:26
 * @since: 1.0
 */
@Slf4j
@Service
public class SessionService {
    private Map<Channel, String> channelToDeviceMap = new ConcurrentHashMap<>();
    private Map<String, Channel> deviceToChannelMap = new ConcurrentHashMap<>();

    @TraceLog
    public void bind(Channel channel, String deviceId) {
        try {
            channelToDeviceMap.put(channel, deviceId);
            deviceToChannelMap.put(deviceId, channel);
        } catch (Exception e) {
            log.error("绑定失败!", e);
        }
    }

    @TraceLog
    public void unbind(Channel channel) {
        try {
            String deviceId = channelToDeviceMap.remove(channel);
            if (deviceId != null) {
                deviceToChannelMap.remove(deviceId);
            }
        } catch (Exception e) {
            log.error("解绑失败!", e);
        }
    }

    public Channel getChannel(String deviceId) {
        try {
            return deviceToChannelMap.get(deviceId);
        } catch (Exception e) {
            log.error("获取连接异常!", e);
            return null;
        }
    }

    public boolean isOnline(String deviceId) {
        return deviceToChannelMap.containsKey(deviceId);
    }
}
