package com.zhangyun.filecloud.common.enums;

import java.util.Objects;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/14 20:10
 * @since: 1.0
 */
public enum ChangeModeEnum {
    CREATE("创建文件"),
    DELETE("删除文件"),
    CHANGE("改变文件"),
    ;

    private final String desc;

    // 构造方法
    ChangeModeEnum(String desc) {
        this.desc = desc;
    }
}
