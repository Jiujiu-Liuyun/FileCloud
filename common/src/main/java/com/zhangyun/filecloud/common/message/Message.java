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
    {
        msgDesc = getClass().getSimpleName();
    }

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;
    /**
     * token，用于认证权限
     */
    private String token;

    /**
     * 设备号
     */
    private String deviceId;

    /**
     * 消息描述
     */
    private String msgDesc;

    /**
     * 返回消息类型
     *
     * @return
     */
    public abstract int getMessageType();

    private int messageType;
    /**
     * 消息指令类型 - 文件标记指令
     */
    public static final int LOGIN_MSG = 5;
    public static final int LOGIN_RESPONSE_MSG = 6;

    public static final int INIT_DEVICE_MSG = 8;
    public static final int INIT_DEVICE_RESPONSE_MSG = 9;
    public static final int LOGOUT_MSG = 10;
    public static final int PING_MSG = 11;
    public static final int FILE_CHANGE_MSG = 12;
    public static final int FILE_TRANSFER_MSG = 13;
    public static final int FILE_TRANSFER_RESPONSE_MSG = 14;
    public static final int RESP_MSG = 15;
    public static final int REQ_FTBO_LIST_MSG = 16;
    public static final int RESP_FTBO_LIST_MSG = 17;
    public static final int NOTIFY_CHANGE_MSG = 18;


    private static final Map<Integer, Class<? extends Message>> MESSAGE_CLASSES = new HashMap<>();

    static {
        MESSAGE_CLASSES.put(LOGIN_MSG, LoginMsg.class);
        MESSAGE_CLASSES.put(LOGIN_RESPONSE_MSG, LoginRespMsg.class);
        MESSAGE_CLASSES.put(INIT_DEVICE_MSG, RegisterDeviceMessage.class);
        MESSAGE_CLASSES.put(INIT_DEVICE_RESPONSE_MSG, RegisterDeviceRespMsg.class);
        MESSAGE_CLASSES.put(LOGOUT_MSG, LogoutMessage.class);
        MESSAGE_CLASSES.put(PING_MSG, PingMessage.class);
        MESSAGE_CLASSES.put(FILE_CHANGE_MSG, FileChangeMessage.class);
        MESSAGE_CLASSES.put(FILE_TRANSFER_MSG, FileTrfMsg.class);
        MESSAGE_CLASSES.put(FILE_TRANSFER_RESPONSE_MSG, FileTrfRespMsg.class);
        MESSAGE_CLASSES.put(RESP_MSG, RespMsg.class);
        MESSAGE_CLASSES.put(REQ_FTBO_LIST_MSG, ReqFTBOMsg.class);
        MESSAGE_CLASSES.put(RESP_FTBO_LIST_MSG, RespFTBOMsg.class);
        MESSAGE_CLASSES.put(NOTIFY_CHANGE_MSG, NotifyChangeMsg.class);
    }

    /**
     * 根据消息类型字节，获得对应的消息 class
     *
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends Message> getMessageClass(int messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    @JSONField(serialize = false)
    private byte[] messageBody;
}
