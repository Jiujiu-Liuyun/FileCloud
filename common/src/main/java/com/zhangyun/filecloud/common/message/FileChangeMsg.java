package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.entity.FileChangeBO;
import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/15 10:41
 * @since: 1.0
 */
@Data
public class FileChangeMsg extends Msg {
    private FileChangeBO fileChangeBO;

    @Override
    public int getMessageType() {
        return FILE_CHANGE_MSG;
    }
}
