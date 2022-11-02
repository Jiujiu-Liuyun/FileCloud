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
public class LoginReseponseMessage extends Message{
    private Integer code;
    private String msg;

    @Override
    public int getMessageType() {
        return LOGIN_RESPONSE_CMD;
    }
}
