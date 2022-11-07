package com.zhangyun.filecloud.client.controller.registerdevice;

import com.zhangyun.filecloud.client.service.ChangeViewService;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/5 22:31
 * @since: 1.0
 */
@FXMLController
public class SelectRootPathController implements Initializable {
    @Autowired
    private ChangeViewService changeViewService;

    private String rootPath = null;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @FXML
    private Label pathShowLabel;
    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void selectPath() {
        DirectoryChooser dirChooser=new DirectoryChooser();
        dirChooser.setTitle("选择根目录路径");
        String curPath = System.getProperty("user.dir");
        dirChooser.setInitialDirectory(new File(curPath));
        Stage stage = (Stage) pathShowLabel.getScene().getWindow();
        File newFolder = dirChooser.showDialog(stage);//这个file就是选择的文件夹了
        if (newFolder != null) {
            pathShowLabel.setText(newFolder.getAbsolutePath());
            rootPath = newFolder.getAbsolutePath();
        }
    }

    /**
     * 1. 跳转视图到输入设备名字
     */
    public void next() {
        if (rootPath == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "请先选择文件存放路径");
            alert.showAndWait();
        } else {
            // 跳转视图
            changeViewService.goDeviceNameView();
        }
    }

    public void cancel() {
        changeViewService.goSelectFolderPathView();
    }
}
