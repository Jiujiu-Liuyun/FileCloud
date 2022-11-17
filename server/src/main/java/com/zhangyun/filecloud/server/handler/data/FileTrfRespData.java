package com.zhangyun.filecloud.server.handler.data;

import com.zhangyun.filecloud.common.message.FileTrfRespMsg;
import lombok.Data;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/17 19:13
 * @since: 1.0
 */
@Data
public class FileTrfRespData {
    /**
     * 数据
     */
    private FileTrfRespMsg fileTrfRespMsg;
    /**
     * 数据是否准备好，true表示已经准备好，false表示正在准备
     */
    private AtomicBoolean isReady = new AtomicBoolean(true);
    /**
     * 获取数据开始时间
     */
    private Long beginTime;
}
