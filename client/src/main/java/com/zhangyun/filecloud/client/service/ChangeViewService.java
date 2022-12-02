package com.zhangyun.filecloud.client.service;

import com.zhangyun.filecloud.client.ClientApplication;
import com.zhangyun.filecloud.client.view.AppView;
import com.zhangyun.filecloud.client.view.LoginView;
import com.zhangyun.filecloud.client.view.RegisterDeviceView;
import com.zhangyun.filecloud.client.view.RegisterUserView;
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

    private final Stage stage = ClientApplication.getStage();

    public void goRegisterUserView() {
        stage.setTitle("用户注册");
        stage.setResizable(false);
        stage.setWidth(300);
        stage.setHeight(200);
        ClientApplication.showView(RegisterUserView.class);
    }

    public void goRegisterDeviceView() {
        // 设备初始化引导界面
        stage.setTitle("设备初始化引导程序");
        stage.setResizable(false);
        stage.setWidth(450);
        stage.setHeight(250);
        ClientApplication.showView(RegisterDeviceView.class);
    }

    public void goAppView() {
        // app主界面
        stage.setTitle("FileCloud");
        stage.setResizable(true);
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
}
