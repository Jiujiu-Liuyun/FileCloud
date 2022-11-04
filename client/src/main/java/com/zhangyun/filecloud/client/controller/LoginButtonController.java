package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.ClientApplication;
import com.zhangyun.filecloud.client.config.Config;
import com.zhangyun.filecloud.client.entity.UserInfo;
import com.zhangyun.filecloud.client.service.LoginService;
import com.zhangyun.filecloud.client.service.ViewService;
import com.zhangyun.filecloud.client.view.AppView;
import com.zhangyun.filecloud.common.message.LoginReseponseMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Autowired
    private AppController appController;

    @FXML
    private TextField textField;
    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textField.setText("zhangyun");
        passwordField.setText("1120");
        ViewService.setLoginView();
    }

    @FXML
    public void login() throws InterruptedException, IOException {
        String username = textField.getText();
        String password = passwordField.getText();
        LoginReseponseMessage reseponseMessage = loginService.sendLoginMessage(username, password);
        if (reseponseMessage == null) {
            log.error("响应消息为null");
            return;
        }
        if (reseponseMessage.getCode() != 200) {
            Alert alert = new Alert(Alert.AlertType.WARNING, reseponseMessage.getMsg());
            alert.showAndWait();
        } else {
            log.info("登录成功");
            // 切换视图
            ClientApplication.showView(AppView.class);
            ViewService.setAppView();
            // 配置文件
            Path dirPath = Config.SETTING_PATH;
            Path filePath = Paths.get(dirPath.toString(), username);
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(username);
            //读取配置：文件路径和设备id
            if (Files.exists(filePath)) {
                userInfo.setDeviceId(FileUtil.getProperty(filePath.toString(), "deviceId"));
                userInfo.setRootPath(FileUtil.getProperty(filePath.toString(), "rootPath"));
            } else {
                // 创建配置文件
                if (!Files.exists(dirPath)) {
                    // 不存在配置文件路径，创建配置文件夹
                    Files.createDirectory(dirPath);
                }
                // 创建用户配置文件
                if (!Files.exists(filePath)) {
                    Files.createFile(filePath);
                }
                // 设置用户信息，设备为初始化，故设为null
                userInfo.setDeviceId(null);
                userInfo.setRootPath(null);
            }
            appController.setUserInfo(userInfo);
        }
    }
}
