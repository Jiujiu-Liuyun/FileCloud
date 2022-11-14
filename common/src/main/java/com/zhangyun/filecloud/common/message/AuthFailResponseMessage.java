package com.zhangyun.filecloud.common.message;

import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/4 16:59
 * @since: 1.0
 */
@Data
public class AuthFailResponseMessage extends Message{
    private Integer code;
    private String msg;

    @Override
    public int getMessageType() {
        return AUTH_FAIL_MSG;
    }

    public AuthFailResponseMessage() {
    }

    public AuthFailResponseMessage(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
