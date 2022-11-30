package com.zhangyun.filecloud.server;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/19 23:24
 * @since: 1.0
 */
public class JavaFxTest extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("are you sure!");
        Optional<ButtonType> buttonType = alert.showAndWait();
        if (buttonType.get() == ButtonType.OK) {
            System.out.println("ok");
        } else if (buttonType.get() == ButtonType.CANCEL) {
            System.out.println("cancel");
        }
    }
}
