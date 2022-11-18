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
public class LoginMsg extends Message {
    private String password;

    @Override
    public int getMessageType() {
        return LOGIN_MSG;
    }
}
