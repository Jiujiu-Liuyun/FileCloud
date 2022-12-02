package com.zhangyun.filecloud.common.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * description: 通知客户端有待更新文件
 *
 * @author: zhangyun
 * @date: 2022/11/18 23:53
 * @since: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotifyChangeMsg extends Msg {
    {
        isNeedLog = false;
    }

    @Override
    public int getMessageType() {
        return NOTIFY_CHANGE_MSG;
    }
}
