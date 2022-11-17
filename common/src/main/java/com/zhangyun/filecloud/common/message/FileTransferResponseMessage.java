package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.entity.FileTransferBO;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/16 10:38
 * @since: 1.0
 */
@Data
public class FileTransferResponseMessage extends Message{
    private Integer code;
    private String desc;
    private StatusEnum statusEnum;
    private Long nextPos;
    @Override
    public int getMessageType() {
        return FILE_TRANSFER_RESPONSE_MSG;
    }
}
