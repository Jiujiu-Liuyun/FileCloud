package com.zhangyun.filecloud.common.message;

import lombok.Data;

/**
 * description: 登录消息
 *
 * @author: zhangyun
 * @date: 2022/11/2 22:31
 * @since: 1.0
 */
@Data
public class RegDeviceMsg extends Msg {
    private String password;
    private String deviceName;
    private String rootPath;

    @Override
    public int getMessageType() {
        return REG_DEVICE_MSG;
    }
}
