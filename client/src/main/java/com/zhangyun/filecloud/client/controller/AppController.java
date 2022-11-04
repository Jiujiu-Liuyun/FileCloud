package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.ClientApplication;
import com.zhangyun.filecloud.client.entity.UserInfo;
import com.zhangyun.filecloud.client.service.SettingService;
import com.zhangyun.filecloud.client.view.AppView;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
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
    private UserInfo userInfo;
    public UserInfo getUserInfo() {
        return userInfo;
    }
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void setFilePath(ActionEvent event) throws IOException {
        Stage settingStage = SettingService.getSingleSettingStage();
        settingStage.show();
    }
}
