package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.RespEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/18 17:06
 * @since: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RespFTBOMsg extends Message{
    private RespEnum respEnum;
    /**
     * 待处理的 FTBO
     */
    private FileTrfBO fileTrfBO;

    @Override
    public int getMessageType() {
        return RESP_FTBO_LIST_MSG;
    }
}

