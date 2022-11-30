package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.enums.FileOperationEnum;
import com.zhangyun.filecloud.common.enums.StatusEnum;
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
    /**
     * 是否完成
     */
    private StatusEnum statusEnum;
    /**
     * 文件相对路径
     */
    private String relativePath;
    /**
     * 传输起始位置
     */
    private Long startPos;

    @Override
    public int getMessageType() {
        return FILE_UPLOAD_MSG;
    }
}
