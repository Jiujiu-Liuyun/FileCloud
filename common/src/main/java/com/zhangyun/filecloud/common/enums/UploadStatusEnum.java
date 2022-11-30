package com.zhangyun.filecloud.common.enums;


import java.util.Objects;

public enum UploadStatusEnum {
    UPLOADING(1, "正在上传"),
    FINISHED(2, "上传完毕"),
    ;

    private Integer code;
    private String desc;

    // 构造方法
    private UploadStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static UploadStatusEnum getTypeName(Integer code) {
        for (UploadStatusEnum status: UploadStatusEnum.values()) {
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
