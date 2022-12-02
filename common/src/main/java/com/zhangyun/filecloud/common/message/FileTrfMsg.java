package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.entity.FileTrfBO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/16 10:38
 * @since: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FileTrfMsg extends Msg {
    private FileTrfBO fileTrfBO;
    @Override
    public int getMessageType() {
        return FILE_TRANSFER_MSG;
    }
}
