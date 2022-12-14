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
     * ????????????????????????
     */
    public void selectPath() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("?????????????????????");
        String curPath;
        if (pathShowLabel.getText() == null || !new File(pathShowLabel.getText()).isDirectory()) {
            curPath = System.getProperty("user.dir") + "/rootPath";
        } else {
            curPath = pathShowLabel.getText();
        }
        // ?????????????????????????????????????????????
        if (!new File(curPath).exists()) {
            try {
                Files.createDirectory(Paths.get(curPath));
            } catch (IOException e) {
                log.error("??????????????????, {}", e.getMessage());
                JavaFXUtil.alertWarning("????????????" + curPath + "??????");
                return;
            }
        }
        // ???????????????????????????????????????
        dirChooser.setInitialDirectory(new File(curPath));
        // ?????????????????????
        Stage stage = (Stage) pathShowLabel.getScene().getWindow();
        File newFolder = dirChooser.showDialog(stage);//??????file???????????????????????????
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
        // ?????????????????????????????????????????????
        RegDeviceRespMsg regDeviceRespMsg = regDeviceService.registerDevice(
                userInfo.getUsername(), userInfo.getPassword(), deviceName, rootPath);
        if (regDeviceRespMsg.getRespEnum() != RespEnum.OK) {
            JavaFXUtil.alertWarning(regDeviceRespMsg.getRespEnum().getDesc());
            return;
        }
        // ??????token
        userInfo.setToken(regDeviceRespMsg.getToken());
        userInfo.setRootPath(rootPath);
        userInfo.setDeviceName(deviceName);

        // ??????????????????
        userInfo.setDeviceId(regDeviceRespMsg.getDeviceId());
        PropertyUtil.setProperty(userInfo.getUsername(), "deviceId", userInfo.getDeviceId());
        PropertyUtil.setProperty(userInfo.getUsername(), "rootPath", userInfo.getRootPath());
        PropertyUtil.setProperty(userInfo.getUsername(), "deviceName", userInfo.getDeviceName());

        // 1. ?????????????????????
        fileMonitorService.startMonitor(appController.getUserInfo().getRootPath(), 1000);
        // 2. ???????????????
        changeViewService.goAppView();
        // 3. ??????????????????
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(new ReqFTBOMsg());
    }

    /**
     * ??????????????????????????????
     *
     * @return
     */
    private boolean auth() {
        // ????????????
        String text = deviceNameField.getText();
        if (text == null) {
            JavaFXUtil.alertWarning("?????????????????????");
            return false;
        }
        if (pathShowLabel.getText().equals("")) {
            JavaFXUtil.alertWarning("??????????????????????????????");
            return false;
        }
        return true;
    }
}
