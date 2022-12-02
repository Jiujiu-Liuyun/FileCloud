package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.entity.UserInfo;
import com.zhangyun.filecloud.client.service.ChangeViewService;
import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.client.service.nettyservice.FileChangeService;
import com.zhangyun.filecloud.client.service.monitor.FileMonitorService;
import com.zhangyun.filecloud.client.service.nettyservice.RegDeviceService;
import com.zhangyun.filecloud.client.utils.JavaFXUtil;
import com.zhangyun.filecloud.client.utils.PropertyUtil;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.RegDeviceRespMsg;
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
public class RegDeviceController implements Initializable {
    @Autowired
    private ChangeViewService changeViewService;
    @Autowired
    private RegDeviceService regDeviceService;
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

    /**
     * 选择文件同步路径
     */
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
                JavaFXUtil.alertWarning("创建目录" + curPath + "失败");
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

    public void cancel() {
        changeViewService.goLoginView();
    }

    public void confirm() throws IOException, InterruptedException {
        if (!auth()) {
            return;
        }
        String rootPath = pathShowLabel.getText();
        String deviceName = deviceNameField.getText();
        UserInfo userInfo = appController.getUserInfo();
        // 发送设备注册消息，获取响应消息
        RegDeviceRespMsg regDeviceRespMsg = regDeviceService.registerDevice(
                userInfo.getUsername(), userInfo.getPassword(), deviceName, rootPath);
        if (regDeviceRespMsg.getRespEnum() != RespEnum.OK) {
            JavaFXUtil.alertWarning(regDeviceRespMsg.getRespEnum().getDesc());
            return;
        }
        // 记录token
        userInfo.setToken(regDeviceRespMsg.getToken());
        userInfo.setRootPath(rootPath);
        userInfo.setDeviceName(deviceName);

        // 写入配置文件
        userInfo.setDeviceId(regDeviceRespMsg.getDeviceId());
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
            JavaFXUtil.alertWarning("请输入设备名称");
            return false;
        }
        if (pathShowLabel.getText().equals("")) {
            JavaFXUtil.alertWarning("请先选择文件存放路径");
            return false;
        }
        return true;
    }
}
