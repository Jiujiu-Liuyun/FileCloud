package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.service.ChangeViewService;
import com.zhangyun.filecloud.client.service.nettyservice.RegUserService;
import com.zhangyun.filecloud.client.utils.JavaFXUtil;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.RegUserRespMsg;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/12/2 14:49
 * @since: 1.0
 */
@FXMLController
@Slf4j
public class RegUserController implements Initializable {
    @Autowired
    private RegUserService regUserService;
    @Autowired
    private ChangeViewService changeViewService;

    @FXML
    private TextField textField;
    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * 注册用户
     */
    public void register() {
        if (textField.getText() == null || passwordField.getText() == null) {
            JavaFXUtil.alertWarning("请将注册信息填写完整");
            return;
        }
        // send to server and get resp
        RegUserRespMsg regUserRespMsg = regUserService.registerUser(textField.getText(), passwordField.getText());
        if (regUserRespMsg == null) {
            JavaFXUtil.alertWarning("服务器无响应，请稍后重试");
            return;
        }
        if (regUserRespMsg.getRespEnum() != RespEnum.OK) {
            JavaFXUtil.alertWarning("regUserRespMsg.getRespEnum().getDesc()");
            return;
        }
        JavaFXUtil.alertInfo("注册成功！");
        changeViewService.goLoginView();
    }

    /**
     * 返回登录界面
     */
    public void returnLog() {
        changeViewService.goLoginView();
    }
}
