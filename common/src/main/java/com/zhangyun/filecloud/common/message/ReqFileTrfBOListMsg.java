package com.zhangyun.filecloud.common.message;

import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/18 17:06
 * @since: 1.0
 */
@Data
public class ReqFileTrfBOListMsg extends Message{


    @Override
    public int getMessageType() {
        return REQ_FTBO_LIST_MSG;
    }
}
