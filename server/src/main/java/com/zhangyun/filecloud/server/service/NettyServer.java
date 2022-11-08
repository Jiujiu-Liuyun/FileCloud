package com.zhangyun.filecloud.server.service;

import com.zhangyun.filecloud.common.protocol.FrameDecoder;
import com.zhangyun.filecloud.common.protocol.MessageCodecSharable;
import com.zhangyun.filecloud.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/15 11:30
 * @since: 1.0
 */
@Slf4j
@Service
public class NettyServer implements ApplicationRunner {

    @Value("${file.server.port}")
    private int serverPort;

    @Value("${file.server.host}")
    private String serverHost;

    @Autowired
    private UploadHandler uploadHandler;
    @Autowired
    private CompareHandler compareHandler;
    @Autowired
    private LoginHandler loginHandler;
    @Autowired
    private MessageFilterHandler messageFilterHandler;
    @Autowired
    private RegisterDeviceHandler registerDeviceHandler;

    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    private NioEventLoopGroup boss = new NioEventLoopGroup();
    private NioEventLoopGroup worker = new NioEventLoopGroup();
    private LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

    @Override
    public void run(ApplicationArguments args) {
        log.info("netty server starting...");
        serverBootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(boss, worker)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FrameDecoder());
                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(messageFilterHandler);
                        ch.pipeline().addLast(uploadHandler);
                        ch.pipeline().addLast(compareHandler);
                        ch.pipeline().addLast(loginHandler);
                        ch.pipeline().addLast(registerDeviceHandler);
                    }
                });
        log.info("netty server start success");
        try {
            Channel channel = serverBootstrap.bind(serverPort).sync().channel();
//            channel.closeFuture().sync();
         } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            boss.shutdownGracefully();
//            worker.shutdownGracefully();
        }
    }

}
