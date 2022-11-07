package com.zhangyun.filecloud.client.service;

import com.zhangyun.filecloud.client.ClientApplication;
import com.zhangyun.filecloud.client.controller.registerdevice.ConfirmController;
import com.zhangyun.filecloud.client.controller.registerdevice.DeviceNameController;
import com.zhangyun.filecloud.client.controller.registerdevice.SelectRootPathController;
import com.zhangyun.filecloud.client.view.AppView;
import com.zhangyun.filecloud.client.view.LoginView;
import com.zhangyun.filecloud.client.view.registerdevice.ConfirmView;
import com.zhangyun.filecloud.client.view.registerdevice.DeviceNameView;
import com.zhangyun.filecloud.client.view.registerdevice.RegisterDeviceView;
import com.zhangyun.filecloud.client.view.registerdevice.SelectRootPathView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/7 10:17
 * @since: 1.0
 */
@Service
@Slf4j
public class ChangeViewService {
    @Autowired
    private SelectRootPathController selectRootPathController;
    @Autowired
    private DeviceNameController deviceNameController;
    @Autowired
    private ConfirmController confirmController;

    private final Stage stage = ClientApplication.getStage();

    public void goInitDeviceView() {
        // 设备初始化引导界面
        stage.setTitle("设备初始化引导程序");
        stage.setResizable(false);
        stage.setHeight(200);
        stage.setWidth(300);
        ClientApplication.showView(RegisterDeviceView.class);
    }

    public void goAppView() {
        // app主界面
        stage.setResizable(true);
        stage.setTitle("FileCloud");
        stage.setHeight(200);
        stage.setWidth(400);
        ClientApplication.showView(AppView.class);
    }

    public void goLoginView() {
        // 登录界面
        stage.setTitle("File Cloud登录页面");
        stage.setResizable(false);
        stage.setHeight(400);
        stage.setWidth(600);
        ClientApplication.showView(LoginView.class);
    }

    public void goSelectFolderPathView() {
        // 同步文件夹选择界面
        stage.setHeight(200);
        stage.setWidth(300);
        stage.setTitle("设备初始化引导程序");
        stage.setResizable(false);
        ClientApplication.showView(SelectRootPathView.class);
    }

    public void goDeviceNameView() {
        // 设备名称设置界面
        stage.setHeight(200);
        stage.setWidth(300);
        stage.setTitle("设备初始化引导程序");
        stage.setResizable(false);
        ClientApplication.showView(DeviceNameView.class);
    }

    public void goConfirmView() {
        // 设备初始化确认界面
        stage.setHeight(200);
        stage.setWidth(300);
        stage.setTitle("设备初始化引导程序");
        stage.setResizable(false);
        ClientApplication.showView(ConfirmView.class);
        // 设置基本信息
        confirmController.getRootPathTextField().setText(selectRootPathController.getRootPath());
        confirmController.getDeviceNameTextField().setText(deviceNameController.getDeviceName());
    }
}
