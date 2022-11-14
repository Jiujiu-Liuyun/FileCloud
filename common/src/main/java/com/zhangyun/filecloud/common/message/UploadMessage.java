package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.enums.FileOperationEnum;
import com.zhangyun.filecloud.common.enums.UploadStatusEnum;
import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/7/31 11:11
 * @since: 1.0
 */
@Data
public class UploadMessage extends Message {
    private UploadStatusEnum statusEnum;
    private FileOperationEnum operationEnum;
    private String filePath;
    private Long startPos;
    private Long lastModified;

    @Override
    public int getMessageType() {
        return FILE_UPLOAD_MSG;
    }
}
