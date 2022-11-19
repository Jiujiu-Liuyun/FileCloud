package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.enums.RespEnum;
import lombok.Data;

/**
 * description: server 响应消息类
 *
 * @author: zhangyun
 * @date: 2022/11/18 16:51
 * @since: 1.0
 */
@Data
public class RespMsg extends Message{
    private RespEnum respEnum;

    @Override
    public int getMessageType() {
        return RESP_MSG;
    }

    public RespMsg() {
    }

    public RespMsg(RespEnum respEnum) {
        this.respEnum = respEnum;
    }
}
