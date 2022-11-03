package com.zhangyun.filecloud.client.service;

import com.zhangyun.filecloud.client.handler.CompareResponseHandler;
import com.zhangyun.filecloud.client.handler.LoginResponseHandler;
import com.zhangyun.filecloud.client.handler.UploadResponseHandler;
import com.zhangyun.filecloud.common.protocol.FrameDecoder;
import com.zhangyun.filecloud.common.protocol.MessageCodecSharable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
public class NettyClient implements ApplicationRunner {
    @Value("${file.server.port}")
    private int serverPort;

    @Value("${file.server.host}")
    private String serverHost;

    @Autowired
    private UploadResponseHandler uploadResponseHandler;
    @Autowired
    private CompareResponseHandler compareResponseHandler;
    @Autowired
    private LoginResponseHandler loginResponseHandler;

    private Bootstrap bootstrap;
    private NioEventLoopGroup group = new NioEventLoopGroup();
    private LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
    private Channel channel = null;

    @Override
    public void run(ApplicationArguments args) {
        log.info("netty client starting...");
        bootstrap = new Bootstrap()
                .channel(NioSocketChannel.class)
                .group(group)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new FrameDecoder());
                        ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(uploadResponseHandler);
                        ch.pipeline().addLast(compareResponseHandler);
                        ch.pipeline().addLast(loginResponseHandler);
                    }
                });
        log.info("netty client start success");
        getChannel();
    }

    public Channel getChannel() {
        // 尝试建立连接
        if (channel == null) {
            try {
                channel = bootstrap.connect(serverHost, serverPort).sync().channel();
            } catch (Exception e) {
                log.info("连接失败: {}", e.getMessage());
            }
            log.info("建立连接：{}", channel);
        }
        return channel;
    }
}
