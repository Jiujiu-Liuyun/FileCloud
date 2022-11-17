package com.zhangyun.filecloud.common.entity;

import com.zhangyun.filecloud.common.enums.*;
import lombok.Data;

/**
 * description: 文件传输对象
 *
 * @author: zhangyun
 * @date: 2022/11/16 10:34
 * @since: 1.0
 */
@Data
public class FileTransferBO {
    private String relativePath;
    private FileTypeEnum fileTypeEnum;
    private OperationTypeEnum operationTypeEnum;
    private String deviceId;
    private TransferModeEnum transferModeEnum;
    private Long startPos;
    private StatusEnum statusEnum;
    /**
     * max read length	消息体最大长度
     */
    private Long maxReadLength;
}
