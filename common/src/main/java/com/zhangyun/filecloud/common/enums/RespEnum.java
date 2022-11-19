package com.zhangyun.filecloud.common.enums;

import java.util.Objects;

/**
 * description: 响应枚举
 *
 * @author: zhangyun
 * @date: 2022/11/15 20:39
 * @since: 1.0
 */
public enum RespEnum {
    OK(1, "ok"),
    MSG_FORMAT_ERROR(2, "msg format is error"),
    USERNAME_NOT_EXIST(3, "username is not exist"),
    PASSWORD_NOT_MATCH(4, "password is not match"),
    REGISTER_DEVICE_FAIL(5, "register device fail"),
    AUTH_TOKEN_FAIL(6, "auth token fail"),
    AUTH_DEVICE_FAIL(7, "auth device fail"),
    LOCK_DEVICE_FAIL(8, "lock device fail"),
    FCR_IS_EMPTY(9, "file change record is empty"),
    ;

    private Integer code;
    private String desc;

    // 构造方法
    private RespEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RespEnum getTypeName(Integer code) {
        for (RespEnum status: RespEnum.values()) {
            if (Objects.equals(status.getCode(), code)) {
                return status;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
