package com.zhangyun.filecloud.client.service;

import com.zhangyun.filecloud.client.ClientApplication;
import javafx.stage.Stage;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/3 19:07
 * @since: 1.0
 */
public class ViewService {
    private static Stage stage;
    static {
        stage = ClientApplication.getStage();
    }

    public static void setLoginView() {
        stage.setTitle("File Cloud登录页面");
        stage.setResizable(false);
    }

    public static void setAppView() {
        stage.setResizable(true);
        stage.setTitle("FileCloud");
        stage.setHeight(200);
        stage.setWidth(400);
    }
}
