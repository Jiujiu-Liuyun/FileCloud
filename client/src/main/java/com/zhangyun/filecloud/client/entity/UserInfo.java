package com.zhangyun.filecloud.client.entity;

import lombok.Data;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/4 00:57
 * @since: 1.0
 */
@Data
public class UserInfo {
    private String username;
    private String deviceId;
    private String rootPath;
    private String token;
    private String deviceName;
}
