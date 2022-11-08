package com.zhangyun.filecloud.client.service.msgmanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/3 22:42
 * @since: 1.0
 */
public class SettingService {
    private static Stage settingStage = null;

    public static Stage getSingleSettingStage() throws IOException {
        if (settingStage == null || !settingStage.isShowing()) {
            settingStage = new Stage();
            Parent root = FXMLLoader.load(Objects.requireNonNull(SettingService.class.getResource("/fxml/Setting.fxml")));
            settingStage.setScene(new Scene(root));
            settingStage.setTitle("设置");
        }
        settingStage.toFront();
        return settingStage;
    }
}
