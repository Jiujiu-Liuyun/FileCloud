package com.zhangyun.filecloud.common.entity;

import com.zhangyun.filecloud.common.enums.ChangeModeEnum;
import com.zhangyun.filecloud.common.enums.UploadStatusEnum;
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
    private String filePath;
    private ChangeModeEnum changeModeEnum;
    private UploadStatusEnum uploadStatusEnum;
    private Long lastModified;
}
