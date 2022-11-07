package com.zhangyun.filecloud.client.controller.registerdevice;

import com.zhangyun.filecloud.client.service.ChangeViewService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/5 21:32
 * @since: 1.0
 */
@FXMLController
@Slf4j
public class RegisterDeviceController implements Initializable {
    @Autowired
    private ChangeViewService changeViewService;

    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void initDevice() {
        changeViewService.goSelectFolderPathView();
    }

    @FXML
    public void showLogin() {
        changeViewService.goLoginView();
    }
}
