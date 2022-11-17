package com.zhangyun.filecloud.common.enums;

import java.util.Objects;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/15 20:39
 * @since: 1.0
 */
public enum TransferModeEnum {
    UPLOAD(1, "上传"),
    DOWNLOAD(2, "下载"),
    ;

    private Integer code;
    private String desc;

    // 构造方法
    private TransferModeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TransferModeEnum getTypeName(Integer code) {
        for (TransferModeEnum status: TransferModeEnum.values()) {
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
