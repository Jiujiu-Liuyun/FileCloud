package com.zhangyun.filecloud.client.controller.registerdevice;

import com.zhangyun.filecloud.client.service.ChangeViewService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/6 20:23
 * @since: 1.0
 */
@FXMLController
@Slf4j
public class DeviceNameController implements Initializable {
    private String deviceName;
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField deviceNameField;

    @Autowired
    private ChangeViewService changeViewService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * 1. 设置设备名
     * 2. 跳转确认界面
     */
    public void next() {
        String text = deviceNameField.getText();
        if (text == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "请输入设备名称");
            alert.showAndWait();
            return;
        }
        deviceName = text;
        changeViewService.goConfirmView();
    }

    public void back() {
        changeViewService.goSelectFolderPathView();
    }
}
