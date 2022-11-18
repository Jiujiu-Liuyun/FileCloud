package com.zhangyun.filecloud.client.controller.login;

import com.zhangyun.filecloud.client.config.Config;
import com.zhangyun.filecloud.client.controller.app.AppController;
import com.zhangyun.filecloud.client.entity.UserInfo;
import com.zhangyun.filecloud.client.service.ChangeViewService;
import com.zhangyun.filecloud.client.service.monitor.FileMonitorService;
import com.zhangyun.filecloud.client.service.msgmanager.LoginService;
import com.zhangyun.filecloud.client.utils.PropertyUtil;
import com.zhangyun.filecloud.common.message.LoginRespMsg;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
public class LoginController implements Initializable {
    @Autowired
    private LoginService loginService;
    @Autowired
    private AppController appController;
    @Autowired
    private ChangeViewService changeViewService;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField textField;
    @FXML
    private PasswordField passwordField;

    @Autowired
    private FileMonitorService fileMonitorService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textField.setText("zhangyun");
        passwordField.setText("1120");
    }

    @FXML
    public void login() throws InterruptedException, IOException {
        String username = textField.getText();
        String password = passwordField.getText();
        // 读取用户配置
        UserInfo userInfo = appController.getUserInfo();
        userInfo.setUsername(username);
        getProperties(username, userInfo);
        // 发送登录信息，获取Server端的响应信息
        LoginRespMsg responseMessage = loginService.login(username, password,
                userInfo.getDeviceId(), userInfo.getRootPath(), userInfo.getDeviceName());

        /**
         * 登录失败
         */
        if (responseMessage == null) {
            log.error("响应消息为null");
            return;
        }
        if (responseMessage.getCode() != 200) {
            Alert alert = new Alert(Alert.AlertType.WARNING, responseMessage.getMsg());
            alert.showAndWait();
        } else {
            /**
             * 登录成功
             */
            log.info("登录成功");
            // 设置token
            userInfo.setToken(responseMessage.getToken());
            /**
             * 切换视图
             */
            if (responseMessage.getIsRegister()) {
                // 启动文件监听器
                fileMonitorService.startMonitor(userInfo.getRootPath(), 1000);
                // 主页面
                changeViewService.goAppView();
            } else {
                // 设备没有注册
                // 设备初始化引导界面
                changeViewService.goInitDeviceView();
            }
        }
    }

    private void getProperties(String username, UserInfo userInfo) throws IOException {
        // 配置文件路径
        Path etcDirPath = Config.SETTING_PATH;
        Path etcFilePath = Paths.get(etcDirPath.toString(), username);
        //读取配置：文件路径和设备id
        if (Files.exists(etcFilePath)) {
            userInfo.setDeviceId(PropertyUtil.getProperty(username, "deviceId"));
            userInfo.setRootPath(PropertyUtil.getProperty(username, "rootPath"));
            userInfo.setDeviceName(PropertyUtil.getProperty(username, "deviceName"));
        } else {
            // 创建配置文件
            if (!Files.exists(etcDirPath)) {
                // 不存在配置文件路径，创建配置文件夹
                Files.createDirectory(etcDirPath);
            }
            // 创建用户配置文件
            if (!Files.exists(etcFilePath)) {
                Files.createFile(etcFilePath);
            }
            // 设置用户信息，设备为初始化，故设为null
            userInfo.setDeviceId(null);
            userInfo.setRootPath(null);
            userInfo.setDeviceName(null);
        }
    }
}
