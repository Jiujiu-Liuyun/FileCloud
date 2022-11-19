package com.zhangyun.filecloud.common.message;

import com.zhangyun.filecloud.common.entity.FileTrfBO;
import lombok.Data;

import java.util.List;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/18 17:06
 * @since: 1.0
 */
@Data
public class RespFileTrfBOListMsg extends Message{
    /**
     * 待处理的 文件传输对象 列表
     */
    private List<FileTrfBO> fileTrfBOS;

    @Override
    public int getMessageType() {
        return REQ_FTBO_LIST_MSG;
    }
}
