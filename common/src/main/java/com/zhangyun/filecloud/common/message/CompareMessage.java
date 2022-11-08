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
public class CompareMessage extends Message{
    private String filePath;

    @Override
    public int getMessageType() {
        return FILE_COMPARE_MSG;
    }
}
