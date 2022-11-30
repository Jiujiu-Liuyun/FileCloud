package com.zhangyun.filecloud.common.enums;

import java.util.Objects;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/15 20:39
 * @since: 1.0
 */
public enum FileTypeEnum {
    FILE(1, "文件"),
    DIRECTORY(2, "目录"),
    ;

    private Integer code;
    private String desc;

    // 构造方法
    private FileTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FileTypeEnum getTypeName(Integer code) {
        for (FileTypeEnum fileTypeEnum: FileTypeEnum.values()) {
            if (Objects.equals(fileTypeEnum.getCode(), code)) {
                return fileTypeEnum;
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
