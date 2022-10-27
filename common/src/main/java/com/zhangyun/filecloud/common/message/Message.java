package com.zhangyun.filecloud.common.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/7/30 02:06
 * @since: 1.0
 */
@Data
public abstract class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    private int messageType;

    /**
     * 返回消息类型
     * @return
     */
    public abstract int getMessageType();
    /**
     * 消息指令类型 - 文件标记指令
     */
    public static final int FILE_TRANSFER_CMD = 1;
    public static final int FILE_TRANSFER_RESPONSE_CMD = 2;


    private static final Map<Integer, Class<? extends Message>> MESSAGE_CLASSES = new HashMap<>();
    static {
        MESSAGE_CLASSES.put(FILE_TRANSFER_CMD, UploadMessage.class);
        MESSAGE_CLASSES.put(FILE_TRANSFER_RESPONSE_CMD, UploadResponseMessage.class);
    }
    /**
     * 根据消息类型字节，获得对应的消息 class
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    @JSONField(serialize = false)
    private byte[] messageBody;
}
