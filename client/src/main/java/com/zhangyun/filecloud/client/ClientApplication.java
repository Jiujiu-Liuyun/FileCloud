package com.zhangyun.filecloud.client;

import com.zhangyun.filecloud.client.service.monitor.FileMonitorService;
import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.client.utils.SpringBeanUtil;
import com.zhangyun.filecloud.client.view.LoginView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zhangyun.filecloud"})
@Slf4j
public class ClientApplication extends AbstractJavaFxApplicationSupport {

    public static void main(String[] args) {
        launch(ClientApplication.class, LoginView.class, args);
//        SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // 设置退出程序过程
        stage.setOnCloseRequest(e -> {
            try {
                // 退出程序
                exitClientApplication();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        super.start(stage);
    }

    public void exitClientApplication() throws Exception {
        // 1. 关闭netty
        NettyClient nettyClient = SpringBeanUtil.getBean(NettyClient.class);
        if (nettyClient == null) {
            throw new RuntimeException("nettyClient获取bean失败");
        }
        nettyClient.shutdownNettyClient();
        // 2. 关闭文件监听器
        FileMonitorService monitor = SpringBeanUtil.getBean(FileMonitorService.class);
        if (monitor == null) {
            throw new RuntimeException("monitor获取bean失败");
        }
        monitor.stopMonitor();
        log.info("=========程序退出=========");
    }
}
