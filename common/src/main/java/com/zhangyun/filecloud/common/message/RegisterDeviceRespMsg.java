package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.enums.RespEnum;
import lombok.Data;

/**
 * description: 登录消息
 *
 * @author: zhangyun
 * @date: 2022/11/2 22:31
 * @since: 1.0
 */
@Data
public class RegisterDeviceRespMsg extends Message {
    private RespEnum respEnum;
    private String deviceId;

    @Override
    public int getMessageType() {
        return INIT_DEVICE_RESPONSE_MSG;
    }
}
