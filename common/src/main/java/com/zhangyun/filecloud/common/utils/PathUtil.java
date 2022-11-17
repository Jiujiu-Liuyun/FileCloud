package com.zhangyun.filecloud.common.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * description: 路径映射工具类
 *
 * @author: zhangyun
 * @date: 2022/11/15 19:22
 * @since: 1.0
 */
public class PathUtil {
    public static Path getAbsolutePath(String relativePath, String rootPath) {
        return Paths.get(rootPath, relativePath);
    }

    public static Path getRelativePath(String absolutePath, String rootPath) {
        return Paths.get(rootPath).relativize(Paths.get(absolutePath));
    }
}
