package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.controller.AppController;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.*;
import com.zhangyun.filecloud.common.message.FileTrfMsg;
import com.zhangyun.filecloud.common.message.RespFTBOMsg;
import com.zhangyun.filecloud.common.utils.FileUtil;
import com.zhangyun.filecloud.common.utils.PathUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description: 服务器端发来FTBO，并对其进行处理
 *
 * @author: zhangyun
 * @date: 2022/11/20 19:24
 * @since: 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class RespFTBOHandler extends SimpleChannelInboundHandler<RespFTBOMsg> {
    @Autowired
    private AppController appController;

    /**
     * 标识是否处于请求链中，如果是，直接抛弃该消息
     */
    private AtomicBoolean inReqChainBoolean = new AtomicBoolean(false);

    public void handlerRefresh() {
        inReqChainBoolean = new AtomicBoolean(false);
    }

    public boolean tryStartReqChain() {
        return inReqChainBoolean.compareAndSet(false, true);
    }

    public boolean tryStopReqChain() {
        return inReqChainBoolean.compareAndSet(true, false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RespFTBOMsg msg) throws Exception {
        // 校验消息
        if (msg == null || msg.getRespEnum() != RespEnum.OK || msg.getFileTrfBO() == null) {
            return;
        }
        log.info("========>>>>>>>> {}", msg);
        // 尝试开启消息链
        boolean setSuccess = tryStartReqChain();
        if (!setSuccess) {
            log.info("已处于消息链中，抛弃该消息 {}", msg);
            return;
        }
        // 处理FTBO并发送FTM
        FileTrfMsg fileTrfMsg = handleFTBO(msg.getFileTrfBO());
        ctx.writeAndFlush(fileTrfMsg);
        log.info("<<<<<<<<======== {}", fileTrfMsg);
    }

    /**
     * 处理FTBO
     * @param fileTrfBO
     * @return
     * @throws IOException
     */
    public FileTrfMsg handleFTBO(FileTrfBO fileTrfBO) throws IOException {
        FileTrfMsg fileTrfMsg = new FileTrfMsg();
        fileTrfMsg.setFileTrfBO(fileTrfBO);
        Path absolutePath = PathUtil.getAbsolutePath(fileTrfBO.getRelativePath(), appController.getUserInfo().getRootPath());
        if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.UPLOAD) {
            // 读
            if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE &&
                    (fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE || fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE)) {
                // 2.2 读出文件，传给客户端
                byte[] bytes = FileUtil.readFile(absolutePath.toString(), fileTrfBO.getStartPos(), fileTrfBO.getMaxReadLength());
                fileTrfMsg.setMessageBody(bytes);
                if (bytes.length < fileTrfBO.getMaxReadLength()) {
                    fileTrfBO.setStatusEnum(StatusEnum.FINISHED);
                } else {
                    fileTrfBO.setStatusEnum(StatusEnum.GOING);
                }
            }
        }
        return fileTrfMsg;
    }

}
