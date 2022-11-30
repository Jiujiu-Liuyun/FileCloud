package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.RespEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;



/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/16 10:38
 * @since: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileTrfRespMsg extends Message{
    private RespEnum respEnum;
    private FileTrfBO fileTrfBO;
    private FileTrfBO nextFileTrfBO;
    private Long nextPos;
    @Override
    public int getMessageType() {
        return FILE_TRANSFER_RESPONSE_MSG;
    }

    public FileTrfRespMsg() {
    }

    public FileTrfRespMsg(RespEnum respEnum) {
        this.respEnum = respEnum;
    }
}
