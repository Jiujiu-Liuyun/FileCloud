package com.zhangyun.filecloud.common.enums;

import java.util.Objects;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/15 20:39
 * @since: 1.0
 */
public enum StatusEnum {
    GOING(1, "正在进行"),
    FINISHED(2, "完毕"),
    ;

    private Integer code;
    private String desc;

    // 构造方法
    private StatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StatusEnum getTypeName(Integer code) {
        for (StatusEnum status: StatusEnum.values()) {
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
