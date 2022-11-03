package com.zhangyun.filecloud.client;

import com.zhangyun.filecloud.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.awt.*;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zhangyun.filecloud"})
public class ClientApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(ClientApplication.class, LoginView.class, args);
//        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
    }
}
