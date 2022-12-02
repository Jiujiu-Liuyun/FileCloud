package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.enums.RespEnum;
import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/2 22:37
 * @since: 1.0
 */
@Data
public class LoginRespMsg extends Msg {
    private RespEnum respBO;
    // 该设备是否注册
    private Boolean isRegister;

    @Override
    public int getMessageType() {
        return LOGIN_RESPONSE_MSG;
    }

    public LoginRespMsg() {
    }

    public LoginRespMsg(RespEnum respBO) {
        this.respBO = respBO;
    }

    public LoginRespMsg(RespEnum respBO, Boolean isRegister) {
        this.respBO = respBO;
        this.isRegister = isRegister;
    }
}
