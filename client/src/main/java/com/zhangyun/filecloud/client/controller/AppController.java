package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.entity.UserInfo;
import com.zhangyun.filecloud.client.handler.RespFTBOHandler;
import com.zhangyun.filecloud.client.service.ChangeViewService;
import com.zhangyun.filecloud.client.service.monitor.FileMonitorService;
import com.zhangyun.filecloud.client.service.nettyservice.LoginService;
import com.zhangyun.filecloud.client.service.nettyservice.RegisterDeviceService;
import com.zhangyun.filecloud.client.service.nettyservice.SettingService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/3 16:02
 * @since: 1.0
 */
@FXMLController
@Slf4j
public class AppController implements Initializable {
    @Autowired
    private ChangeViewService changeViewService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private LoginController loginController;
    @Autowired
    private FileMonitorService fileMonitorService;
    @Autowired
    private RegisterDeviceService registerDeviceService;
    @Autowired
    private RespFTBOHandler respFTBOHandler;

    private UserInfo userInfo = new UserInfo();

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setFilePath(ActionEvent event) throws IOException {
        Stage settingStage = SettingService.getSingleSettingStage();
        settingStage.show();
    }

    public void logout() {
        // 1. 发送登出消息
        loginService.logout(userInfo.getUsername());
        // 清空用户信息
        userInfo = null;
        // 3. 关闭文件监听器
        fileMonitorService.stopMonitor();
        // refresh service
        loginService.serviceRefresh();
        registerDeviceService.serviceRefresh();
        respFTBOHandler.handlerRefresh();
        // 4.切换视图
        changeViewService.goLoginView();
    }
}
