package com.zhangyun.filecloud.common.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: 注册用户
 *
 * @author: zhangyun
 * @date: 2022/12/2 12:00
 * @since: 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegUserMsg extends Msg {
    private String password;

    @Override
    public int getMessageType() {
        return REG_USER_MSG;
    }
}
