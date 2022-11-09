package com.zhangyun.filecloud.server.service;

import com.zhangyun.filecloud.common.protocol.FrameDecoder;
import com.zhangyun.filecloud.common.protocol.MessageCodecSharable;
import com.zhangyun.filecloud.server.handler.*;
import com.zhangyun.filecloud.server.service.session.SessionService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
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
    private SessionService sessionService;

    @Autowired
    private UploadHandler uploadHandler;
    @Autowired
    private CompareHandler compareHandler;
    @Autowired
    private LoginHandler loginHandler;
    @Autowired
    private MessageFilterHandler MESSAGE_FILTER_HANDLER;
    @Autowired
    private RegisterDeviceHandler registerDeviceHandler;
    @Autowired
    private LogoutHandler logoutHandler;
    @Autowired
    private QuitHandler QUIT_HANDLER;

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

                        // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
                        ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
                        // ChannelDuplexHandler 可以同时作为入站和出站处理器
                        ch.pipeline().addLast(new ChannelDuplexHandler() {
                            // 用来触发特殊事件
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception{
                                IdleStateEvent event = (IdleStateEvent) evt;
                                // 触发了读空闲事件
                                if (event.state() == IdleState.READER_IDLE) {
                                    log.debug("已经 5s 没有读到数据了");
                                    ctx.channel().close();
                                    sessionService.unbind(ctx.channel());
                                }
                            }
                        });

                        ch.pipeline().addLast(MESSAGE_FILTER_HANDLER);
                        ch.pipeline().addLast(uploadHandler);
                        ch.pipeline().addLast(compareHandler);
                        ch.pipeline().addLast(loginHandler);
                        ch.pipeline().addLast(registerDeviceHandler);
                        ch.pipeline().addLast(logoutHandler);                   // 登出


                        ch.pipeline().addLast(QUIT_HANDLER);        // 连接断开处理
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
