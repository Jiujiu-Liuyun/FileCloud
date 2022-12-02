package com.zhangyun.filecloud.client.utils;

import javafx.scene.control.Alert;

/**
 * description: javafx 工具类
 *
 * @author: zhangyun
 * @date: 2022/12/2 16:50
 * @since: 1.0
 */
public class JavaFXUtil {

    public static void alertWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg);
        alert.showAndWait();
    }

    public static void alertInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.showAndWait();
    }
}
