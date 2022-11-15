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
     * @return
     */
    public abstract int getMessageType();
    private int messageType;
    /**
     * 消息指令类型 - 文件标记指令
     */
    public static final int FILE_UPLOAD_MSG = 1;
    public static final int FILE_UPLOAD_RESPONSE_MSG = 2;
    public static final int FILE_COMPARE_MSG = 3;
    public static final int FILE_COMPARE_RESPONSE_MSG = 4;
    public static final int LOGIN_MSG = 5;
    public static final int LOGIN_RESPONSE_MSG = 6;
    public static final int AUTH_FAIL_MSG = 7;
    public static final int INIT_DEVICE_MSG = 8;
    public static final int INIT_DEVICE_RESPONSE_MSG = 9;
    public static final int LOGOUT_MSG = 10;
    public static final int PING_MSG = 11;
    public static final int FILE_CHANGE_MSG = 12;


    private static final Map<Integer, Class<? extends Message>> MESSAGE_CLASSES = new HashMap<>();
    static {
        MESSAGE_CLASSES.put(FILE_UPLOAD_MSG, UploadMessage.class);
        MESSAGE_CLASSES.put(FILE_UPLOAD_RESPONSE_MSG, UploadResponseMessage.class);
        MESSAGE_CLASSES.put(FILE_COMPARE_MSG, CompareMessage.class);
        MESSAGE_CLASSES.put(FILE_COMPARE_RESPONSE_MSG, CompareResponseMessage.class);
        MESSAGE_CLASSES.put(LOGIN_MSG, LoginMessage.class);
        MESSAGE_CLASSES.put(LOGIN_RESPONSE_MSG, LoginResponseMessage.class);
        MESSAGE_CLASSES.put(AUTH_FAIL_MSG, AuthFailResponseMessage.class);
        MESSAGE_CLASSES.put(INIT_DEVICE_MSG, RegisterDeviceMessage.class);
        MESSAGE_CLASSES.put(INIT_DEVICE_RESPONSE_MSG, RegisterDeviceResponseMessage.class);
        MESSAGE_CLASSES.put(LOGOUT_MSG, LogoutMessage.class);
        MESSAGE_CLASSES.put(PING_MSG, PingMessage.class);
        MESSAGE_CLASSES.put(FILE_CHANGE_MSG, FileChangeMessage.class);
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
