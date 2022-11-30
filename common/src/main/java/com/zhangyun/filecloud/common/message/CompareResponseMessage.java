package com.zhangyun.filecloud.common.message;

import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/27 21:43
 * @since: 1.0
 */
@Data
public class CompareResponseMessage extends Message{
    public static final int FILE = 0;
    public static final int DIRECTORY = 1;

    private String filePath;
    private Boolean isExist;
    private Integer type;
    private Long lastModified;
    private String[] fileList;
    private String md5;

    @Override
    public int getMessageType() {
        return FILE_COMPARE_RESPONSE_MSG;
    }
}
