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
    private Map<Channel, String> channelUsernameMap = new ConcurrentHashMap<>();
    private Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();

    @TraceLog
    public void bind(Channel channel, String username) {
        channelUsernameMap.put(channel, username);
        usernameChannelMap.put(username, channel);
    }

    @TraceLog
    public void unbind(Channel channel) {
        String username = channelUsernameMap.remove(channel);
        if (username != null) {
            usernameChannelMap.remove(username);
        }
    }

    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }

}
