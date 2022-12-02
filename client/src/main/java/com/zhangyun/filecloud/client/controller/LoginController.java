package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.config.ClientConfig;
import com.zhangyun.filecloud.client.entity.UserInfo;
import com.zhangyun.filecloud.client.service.ChangeViewService;
import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.client.service.nettyservice.FileChangeService;
import com.zhangyun.filecloud.client.service.monitor.FileMonitorService;
import com.zhangyun.filecloud.client.service.nettyservice.LoginService;
import com.zhangyun.filecloud.client.service.nettyservice.RegisterDeviceService;
import com.zhangyun.filecloud.client.utils.PropertyUtil;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.LoginRespMsg;
import com.zhangyun.filecloud.common.message.ReqFTBOMsg;
import de.felixroske.jfxsupport.FXMLController;
import io.netty.channel.Channel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
import java.util.Optional;
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
    @Autowired
    private FileChangeService fileChangeService;
    @Autowired
    private RegisterDeviceService registerDeviceService;
    @Autowired
    private NettyClient nettyClient;

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
    public void login() throws IOException {
        String username = textField.getText();
        String password = passwordField.getText();
        // 读取用户配置
        UserInfo userInfo = getProperties(username);
        userInfo.setUsername(username);
        appController.setUserInfo(userInfo);
        // 发送登录信息，获取Server端的响应信息
        LoginRespMsg responseMessage = loginService.login(username, password, userInfo.getDeviceId());

        // 登录失败
        if (responseMessage == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "服务器连接超时，请检查网络，或稍后重试");
            alert.showAndWait();
            return;
        }
        // 验证失败
        if (responseMessage.getRespBO() != RespEnum.OK) {
            Alert alert = new Alert(Alert.AlertType.WARNING, responseMessage.getRespBO().getDesc());
            alert.showAndWait();
            return;
        } else {
            // 登录成功
            log.info("登录成功");
        }

        // 设备已注册
        if (responseMessage.getIsRegister()) {
            // 设置token
            userInfo.setToken(responseMessage.getToken());
            // 1. 启动文件监听器
            fileMonitorService.startMonitor(appController.getUserInfo().getRootPath(), 1000);
            // 2. 跳转主页面
            changeViewService.goAppView();
            // 3. 请求文件变化
            Channel channel = nettyClient.getChannel();
            channel.writeAndFlush(new ReqFTBOMsg());
        } else {
            // 注册提示框
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("该设备尚未注册，是否将此设备注册到服务器？");
            Optional<ButtonType> buttonType = alert.showAndWait();
            if (buttonType.get() == ButtonType.OK) {
                // 跳转注册
                // 设备初始化引导界面
                changeViewService.goRegisterDeviceView();
            }
        }
    }

    /**
     * 读取用户配置
     *
     * @param username
     * @return
     * @throws IOException
     */
    private UserInfo getProperties(String username) throws IOException {
        UserInfo userInfo = new UserInfo();
        // 配置文件路径
        Path etcDirPath = ClientConfig.SETTING_PATH;
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
                Files.createDirectories(etcDirPath);
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
        return userInfo;
    }

}
