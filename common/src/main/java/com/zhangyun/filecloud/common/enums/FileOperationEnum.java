package com.zhangyun.filecloud.common.enums;

import java.util.Objects;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/15 23:49
 * @since: 1.0
 */
public enum FileOperationEnum {
    DIRECTORY_CREATE(0, ""),
    DIRECTORY_CHANGE(1, ""),
    DIRECTORY_DELETE(2, ""),

    FILE_CREATE(3, ""),
    FILE_CHANGE(4, ""),
    FILE_DELETE(5, ""),
    ;

    private Integer code;
    private String desc;

    // 构造方法
    private FileOperationEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FileOperationEnum getTypeName(Integer code) {
        for (FileOperationEnum status: FileOperationEnum.values()) {
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
