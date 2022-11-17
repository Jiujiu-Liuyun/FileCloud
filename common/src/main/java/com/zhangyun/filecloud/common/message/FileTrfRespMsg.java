package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.entity.FileTransferBO;
import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/16 10:38
 * @since: 1.0
 */
@Data
public class FileTrfRespMsg extends Message{
    private Integer code;
    private String desc;
    private FileTransferBO fileTransferBO;
    private Long nextPos;
    @Override
    public int getMessageType() {
        return FILE_TRANSFER_RESPONSE_MSG;
    }
}
