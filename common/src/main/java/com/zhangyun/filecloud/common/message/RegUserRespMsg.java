package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.enums.RespEnum;
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
public class RegUserRespMsg extends Msg {
    private RespEnum respEnum;

    @Override
    public int getMessageType() {
        return REG_USER_RESP_MSG;
    }

    public RegUserRespMsg() {
    }

    public RegUserRespMsg(RespEnum respEnum) {
        this.respEnum = respEnum;
    }
}
