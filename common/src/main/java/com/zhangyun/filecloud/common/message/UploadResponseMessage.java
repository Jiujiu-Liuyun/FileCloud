package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.enums.FileOperationEnum;
import com.zhangyun.filecloud.common.enums.FileStatusEnum;
import lombok.Data;

/**
 * description:
 *
 * @program: portalknight
 * @author: zhangyun
 * @date: 2022-08-01 11:00
 **/
@Data
public class UploadResponseMessage extends Message{
    private String filePath;
    private FileStatusEnum statusEnum;
    private FileOperationEnum operationEnum;
    private Long nextPos;

    @Override
    public int getMessageType() {
        return FILE_UPLOAD_RESPONSE_MSG;
    }
}
