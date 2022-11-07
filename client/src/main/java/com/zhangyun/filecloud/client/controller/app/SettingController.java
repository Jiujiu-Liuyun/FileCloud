package com.zhangyun.filecloud.client.controller.app;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/3 21:07
 * @since: 1.0
 */
@FXMLController
public class SettingController implements Initializable {
    @FXML
    private Label pathShowLabel;
    @FXML
    private Button pathChooseButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void dirSelect() {
        DirectoryChooser dirChooser=new DirectoryChooser();
        dirChooser.setTitle("选择根目录路径");
        String rootPath = pathShowLabel.getText();
        dirChooser.setInitialDirectory(new File(rootPath));
        Stage stage = (Stage) pathChooseButton.getScene().getWindow();
        File newFolder = dirChooser.showDialog(stage);//这个file就是选择的文件夹了
        pathShowLabel.setText(newFolder.getAbsolutePath());
    }
}
