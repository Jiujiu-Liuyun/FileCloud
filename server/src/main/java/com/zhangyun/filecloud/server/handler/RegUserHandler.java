package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.RegUserMsg;
import com.zhangyun.filecloud.common.message.RegUserRespMsg;
import com.zhangyun.filecloud.server.database.entity.User;
import com.zhangyun.filecloud.server.database.service.UserService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/12/2 14:31
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class RegUserHandler extends SimpleChannelInboundHandler<RegUserMsg> {
    @Autowired
    private UserService userService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegUserMsg msg) throws Exception {
        log.info("========>>>>>>>> {}", msg);
        RespEnum respEnum = authMsg(msg);
        if (respEnum != RespEnum.OK) {
            ctx.writeAndFlush(new RegUserRespMsg(respEnum));
            return;
        }
        User user = userService.selectUserByName(msg.getUsername());
        if (user != null) {
            ctx.writeAndFlush(new RegUserRespMsg(RespEnum.USER_HAS_BEEN_REGISTERED));
            return;
        }
        boolean create = userService.createUser(msg.getUsername(), msg.getPassword());
        if (create) {
            ctx.writeAndFlush(new RegUserRespMsg(RespEnum.OK));
        } else {
            ctx.writeAndFlush(new RegUserRespMsg(RespEnum.REGISTER_USER_FAIL));
        }
    }

    /**
     * 校验消息格式
     * @param msg
     * @return
     */
    private RespEnum authMsg(RegUserMsg msg) {
        if (msg == null || msg.getUsername() == null || msg.getPassword() == null) {
            return RespEnum.MSG_FORMAT_ERROR;
        }
        return RespEnum.OK;
    }
}
