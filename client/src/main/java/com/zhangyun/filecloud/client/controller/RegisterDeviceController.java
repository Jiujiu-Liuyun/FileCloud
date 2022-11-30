package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.entity.UserInfo;
import com.zhangyun.filecloud.client.service.ChangeViewService;
import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.client.service.nettyservice.FileChangeService;
import com.zhangyun.filecloud.client.service.monitor.FileMonitorService;
import com.zhangyun.filecloud.client.service.nettyservice.RegisterDeviceService;
import com.zhangyun.filecloud.client.utils.PropertyUtil;
import com.zhangyun.filecloud.common.message.RegisterDeviceRespMsg;
import com.zhangyun.filecloud.common.message.ReqFTBOMsg;
import de.felixroske.jfxsupport.FXMLController;
import io.netty.channel.Channel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/20 00:50
 * @since: 1.0
 */
@FXMLController
@Slf4j
public class RegisterDeviceController implements Initializable {
    @Autowired
    private ChangeViewService changeViewService;
    @Autowired
    private RegisterDeviceService registerDeviceService;
    @Autowired
    private FileMonitorService fileMonitorService;
    @Autowired
    private AppController appController;
    @Autowired
    private FileChangeService fileChangeService;
    @Autowired
    private LoginController loginController;
    @Autowired
    private NettyClient nettyClient;

    @FXML
    private TextField deviceNameField;
    @FXML
    private Label pathShowLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void selectPath() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("选择根目录路径");
        String curPath;
        if (pathShowLabel.getText() == null || !new File(pathShowLabel.getText()).isDirectory()) {
            curPath = System.getProperty("user.dir") + "/rootPath";
        } else {
            curPath = pathShowLabel.getText();
        }
        // 判断路径是否存在，不存在则创建
        if (!new File(curPath).exists()) {
            try {
                Files.createDirectory(Paths.get(curPath));
            } catch (IOException e) {
                log.error("创建目录失败, {}", e.getMessage());
                Alert alert = new Alert(Alert.AlertType.WARNING, "创建目录" + curPath + "失败");
                alert.showAndWait();
                return;
            }
        }
        // 设置文件夹选择器的初始路径
        dirChooser.setInitialDirectory(new File(curPath));
        // 选择文件夹路径
        Stage stage = (Stage) pathShowLabel.getScene().getWindow();
        File newFolder = dirChooser.showDialog(stage);//这个file就是选择的文件夹了
        if (newFolder != null) {
            pathShowLabel.setText(newFolder.getAbsolutePath());
        }
    }

    @FXML
    public void cancel() {
        changeViewService.goLoginView();
    }

    @FXML
    public void confirm() throws IOException, InterruptedException {
        if (!auth()) {
            return;
        }
        UserInfo userInfo = appController.getUserInfo();
        userInfo.setRootPath(pathShowLabel.getText());
        userInfo.setDeviceName(deviceNameField.getText());
        // 发送设备注册消息，获取响应消息
        RegisterDeviceRespMsg registerDeviceRespMsg = registerDeviceService.registerDevice(
                userInfo.getUsername(), userInfo.getDeviceName(), userInfo.getRootPath());
        // 记录token
        userInfo.setToken(registerDeviceRespMsg.getToken());

        // 写入配置文件
        userInfo.setDeviceId(registerDeviceRespMsg.getDeviceId());
        PropertyUtil.setProperty(userInfo.getUsername(), "deviceId", userInfo.getDeviceId());
        PropertyUtil.setProperty(userInfo.getUsername(), "rootPath", userInfo.getRootPath());
        PropertyUtil.setProperty(userInfo.getUsername(), "deviceName", userInfo.getDeviceName());

        // 1. 启动文件监听器
        fileMonitorService.startMonitor(appController.getUserInfo().getRootPath(), 1000);
        // 2. 跳转主页面
        changeViewService.goAppView();
        // 3. 请求文件变化
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(new ReqFTBOMsg());
    }

    /**
     * 校验输入参数是否正确
     *
     * @return
     */
    private boolean auth() {
        // 校验参数
        String text = deviceNameField.getText();
        if (text == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "请输入设备名称");
            alert.showAndWait();
            return false;
        }
        if (pathShowLabel.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "请先选择文件存放路径");
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
