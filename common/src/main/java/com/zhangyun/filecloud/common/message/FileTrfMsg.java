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
public class FileTrfMsg extends Message{
    private FileTransferBO fileTransferBO;
    @Override
    public int getMessageType() {
        return FILE_TRANSFER_MSG;
    }
}
