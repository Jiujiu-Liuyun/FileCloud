package com.zhangyun.filecloud.client.controller.registerdevice;

import com.zhangyun.filecloud.client.controller.app.AppController;
import com.zhangyun.filecloud.client.entity.UserInfo;
import com.zhangyun.filecloud.client.service.ChangeViewService;
import com.zhangyun.filecloud.client.service.msgmanager.RegisterDeviceService;
import com.zhangyun.filecloud.client.utils.PropertyUtil;
import com.zhangyun.filecloud.common.message.RegisterDeviceResponseMessage;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/7 00:07
 * @since: 1.0
 */
@FXMLController
public class ConfirmController implements Initializable {
    @Autowired
    private RegisterDeviceService registerDeviceService;

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label rootPathTextField;
    public Label getRootPathTextField() {
        return rootPathTextField;
    }

    @FXML
    private Label deviceNameTextField;
    public Label getDeviceNameTextField() {
        return deviceNameTextField;
    }

    @Autowired
    private DeviceNameController deviceNameController;
    @Autowired
    private SelectRootPathController selectRootPathController;
    @Autowired
    private ChangeViewService changeViewService;
    @Autowired
    private AppController appController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * 1. 向Server发送设备注册消息
     * 2. 跳转到主页面
     */
    public void ok() throws InterruptedException, IOException {
        UserInfo userInfo = appController.getUserInfo();
        userInfo.setRootPath(selectRootPathController.getRootPath());
        userInfo.setDeviceName(deviceNameController.getDeviceName());
        // 发送设备注册消息，获取响应消息
        RegisterDeviceResponseMessage registerDeviceResponseMessage = registerDeviceService.registerDevice(
                userInfo.getUsername(), userInfo.getDeviceName(), userInfo.getToken(), userInfo.getRootPath());
        // 记录token
        userInfo.setToken(registerDeviceResponseMessage.getToken());

        // 写入配置文件
        userInfo.setDeviceId(registerDeviceResponseMessage.getDeviceId());
        PropertyUtil.setProperty(userInfo.getUsername(), "deviceId", userInfo.getDeviceId());
        PropertyUtil.setProperty(userInfo.getUsername(), "rootPath", userInfo.getRootPath());
        PropertyUtil.setProperty(userInfo.getUsername(), "deviceName", userInfo.getDeviceName());
        // 跳转主界面
        changeViewService.goAppView();
    }

    public void back() {
        changeViewService.goDeviceNameView();
    }
}
