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
public class LoginMessage extends Message {
    private String password;
    private String deviceId;
    private String rootPath;
    private String deviceName;

    @Override
    public int getMessageType() {
        return LOGIN_MSG;
    }
}
