package com.zhangyun.filecloud.common.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: 登录消息
 *
 * @author: zhangyun
 * @date: 2022/11/2 22:31
 * @since: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginMsg extends Msg {
    private String password;

    @Override
    public int getMessageType() {
        return LOGIN_MSG;
    }
}
