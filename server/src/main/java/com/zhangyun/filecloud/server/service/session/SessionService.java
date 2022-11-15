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
        channelToDeviceMap.put(channel, deviceId);
        deviceToChannelMap.put(deviceId, channel);
    }

    @TraceLog
    public void unbind(Channel channel) {
        String deviceId = channelToDeviceMap.remove(channel);
        if (deviceId != null) {
            deviceToChannelMap.remove(deviceId);
        }
    }

    public Channel getChannel(String username) {
        return deviceToChannelMap.get(username);
    }

}
