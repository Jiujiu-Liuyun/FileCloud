package com.zhangyun.filecloud.common.message;

import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/2 22:37
 * @since: 1.0
 */
@Data
public class LoginResponseMessage extends Message{
    private Integer code;
    private String msg;
    private String token;
    // 该设备是否注册
    private Boolean isRegister;

    @Override
    public int getMessageType() {
        return LOGIN_RESPONSE_MSG;
    }

    public LoginResponseMessage() {
    }

    public LoginResponseMessage(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
