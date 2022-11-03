package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.ClientApplication;
import com.zhangyun.filecloud.client.service.LoginService;
import com.zhangyun.filecloud.client.view.AppView;
import com.zhangyun.filecloud.common.message.LoginReseponseMessage;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/2 15:25
 * @since: 1.0
 */
@FXMLController
@Slf4j
public class LoginButtonController implements Initializable {
    @Autowired
    private LoginService loginService;

    @FXML
    private TextField textField;
    @FXML
    private PasswordField passwordField;
    private Stage stage;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = ClientApplication.getStage();
        stage.setTitle("File Cloud登录页面");
        stage.setResizable(false);
    }

    @FXML
    public void login() throws InterruptedException {
        LoginReseponseMessage reseponseMessage = loginService.sendLoginMessage(textField.getText(), passwordField.getText());
        if (reseponseMessage == null) {
            log.error("响应消息为null");
            return;
        }
        if (reseponseMessage.getCode() != 200) {
            Alert alert = new Alert(Alert.AlertType.WARNING, reseponseMessage.getMsg());
            alert.showAndWait();
        } else {
            log.info("登录成功");
            ClientApplication.showView(AppView.class);
            stage.setResizable(true);
            stage.setTitle("FileCloud");
        }
    }
}
