package com.zhangyun.filecloud.client.service;

import com.zhangyun.filecloud.client.config.ClientConfig;
import com.zhangyun.filecloud.client.handler.*;
import com.zhangyun.filecloud.common.message.PingMessage;
import com.zhangyun.filecloud.common.message.ReqFTBOMsg;
import com.zhangyun.filecloud.common.protocol.FrameDecoder;
import com.zhangyun.filecloud.common.protocol.MessageCodecSharable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/15 11:30
 * @since: 1.0
 */
@Slf4j
@Service
public class NettyClient {
    @Value("${file.server.port}")
    private int serverPort;
    @Value("${file.server.host}")
    private String serverHost;

    @Autowired
    private LoginRespHandler loginRespHandler;
    @Autowired
    private RegisterDeviceRespHandler registerDeviceRespHandler;
    @Autowired
    private FileTrfRespMsgHandler fileTrfRespMsgHandler;
    @Autowired
    private RespFTBOHandler respFTBOHandler;
    @Autowired
    private OutBoundHandler OUT_BOUND_HANDLER;
    private LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

    private Bootstrap bootstrap;
    private NioEventLoopGroup group = new NioEventLoopGroup();
    private Channel channel = null;

    @PostConstruct
    public void initNetty() {
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

                        // 出站处理器 添加token username deviceId
                        ch.pipeline().addLast(OUT_BOUND_HANDLER);

                        // 读空闲，会触发一个 IdleState#WRITER_IDLE 事件
                        ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                        ch.pipeline().addLast(new ChannelDuplexHandler() {
                            // 用来触发特殊事件
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                                IdleStateEvent event = (IdleStateEvent) evt;
                                // 触发了读空闲事件
                                if (event.state() == IdleState.READER_IDLE) {
                                    // 读空闲，向服务端请求FTBO
                                    ctx.writeAndFlush(new ReqFTBOMsg());
                                }
                            }
                        });

                        ch.pipeline().addLast(loginRespHandler);
                        ch.pipeline().addLast(registerDeviceRespHandler);
                        ch.pipeline().addLast(fileTrfRespMsgHandler);
                        ch.pipeline().addLast(respFTBOHandler);
                    }
                });
        log.info("netty client start success");
    }

    public Channel getChannel() {
        // 尝试建立连接
        if (channel == null || !channel.isActive()) {
            try {
                channel = bootstrap.connect(serverHost, serverPort).sync().channel();
            } catch (Exception e) {
                log.info("连接失败: {}", e.getMessage());
            }
            log.info("建立连接：{}", channel);
        }
        return channel;
    }

    public void closeChannel() throws InterruptedException {
        if (channel != null && channel.isActive()) {
            channel.close().sync();
            log.info("关闭连接 {}", channel);
            channel = null;
        }
    }

    public void shutdownNettyClient() throws InterruptedException {
        // 关闭连接
        closeChannel();
        // 关闭group
        group.shutdownGracefully();
    }
}
