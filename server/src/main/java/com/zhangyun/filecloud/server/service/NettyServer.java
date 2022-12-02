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
public class NettyServer {
    @Value("${fileCloud.server.nettyPort}")
    private int serverPort;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private RegUserHandler REG_USER_HANDLER;
    @Autowired
    private LoginHandler LOGIN_HANDLER;
    @Autowired
    private RegDeviceHandler REGISTER_DEVICE_HANDLER;
    @Autowired
    private AuthTokenHandler AUTH_TOKEN_HANDLER;
    @Autowired
    private AuthDeviceHandler AUTH_DEVICE_HANDLER;
    @Autowired
    private FileChangeHandler FILE_CHANGE_HANDLER;
    @Autowired
    private FileTrfMsgHandler FILE_TRF_MSG_HANDLER;
    @Autowired
    private ReqFTBOHandler REQ_FTBO_HANDLER;
    @Autowired
    private LogoutHandler LOGOUT_HANDLER;
    @Autowired
    private GlobalExceptionHandler GLOBAL_EXCEPTION_HANDLER;

    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    private NioEventLoopGroup boss = new NioEventLoopGroup();
    private NioEventLoopGroup worker = new NioEventLoopGroup();
    private LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

    @PostConstruct
    public void startNetty() {
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
//                        ch.pipeline().addLast(new IdleStateHandler(5, 0, 0));
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

                        // 0. 注册
                        ch.pipeline().addLast(REG_USER_HANDLER);
                        // 1. 登录认证
                        ch.pipeline().addLast(LOGIN_HANDLER);
                        // 2. 注册设备
                        ch.pipeline().addLast(REGISTER_DEVICE_HANDLER);
                        // 3. token认证
                        ch.pipeline().addLast(AUTH_TOKEN_HANDLER);
                        // 4. 设备号认证
                        ch.pipeline().addLast(AUTH_DEVICE_HANDLER);

                        ch.pipeline().addLast(FILE_CHANGE_HANDLER);
                        ch.pipeline().addLast(FILE_TRF_MSG_HANDLER);
                        ch.pipeline().addLast(REQ_FTBO_HANDLER);

                        ch.pipeline().addLast(LOGOUT_HANDLER);                   // 登出
                        ch.pipeline().addLast(GLOBAL_EXCEPTION_HANDLER);        // 连接断开处理
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
