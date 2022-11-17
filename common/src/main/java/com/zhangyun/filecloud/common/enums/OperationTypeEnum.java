package com.zhangyun.filecloud.common.enums;

import java.util.Objects;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/14 20:10
 * @since: 1.0
 */
public enum OperationTypeEnum {
    CREATE(1, "创建"),
    DELETE(2, "删除"),
    CHANGE(3, "改变"),
    ;

    private Integer code;
    private String desc;

    // 构造方法
    OperationTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OperationTypeEnum getTypeName(Integer code) {
        for (OperationTypeEnum operationTypeEnum: OperationTypeEnum.values()) {
            if (Objects.equals(operationTypeEnum.getCode(), code)) {
                return operationTypeEnum;
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
