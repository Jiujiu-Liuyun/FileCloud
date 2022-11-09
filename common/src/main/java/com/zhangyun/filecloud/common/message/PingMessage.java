package com.zhangyun.filecloud.common.message;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/9 14:05
 * @since: 1.0
 */
public class PingMessage extends Message{
    @Override
    public int getMessageType() {
        return PING_MSG;
    }
}
