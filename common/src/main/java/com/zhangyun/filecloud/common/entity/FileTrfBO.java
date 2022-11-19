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
public class FileTrfBO {
    private String relativePath;
    private FileTypeEnum fileTypeEnum;
    private OperationTypeEnum operationTypeEnum;
    private String deviceId;
    private TransferModeEnum transferModeEnum;
    /**
     * 传输起始位置（传输时需要改变）
     */
    private Long startPos;
    /**
     * 传输状态（传输时需要改变）
     */
    private StatusEnum statusEnum;
    private Long maxReadLength;
}
