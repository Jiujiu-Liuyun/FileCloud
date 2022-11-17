package com.zhangyun.filecloud.common.entity;

import com.zhangyun.filecloud.common.enums.FileTypeEnum;
import com.zhangyun.filecloud.common.enums.OperationTypeEnum;
import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/14 20:09
 * @since: 1.0
 */
@Data
public class FileChangeBO {
    private String relativePath;
    private FileTypeEnum fileTypeEnum;
    private OperationTypeEnum operationTypeEnum;
}
