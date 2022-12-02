package com.zhangyun.filecloud.common.message;

import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/8 16:59
 * @since: 1.0
 */
@Data
public class LogoutMsg extends Msg {
    @Override
    public int getMessageType() {
        return LOGOUT_MSG;
    }
}
