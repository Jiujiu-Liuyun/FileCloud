package com.zhangyun.filecloud.client.handler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.zhangyun.filecloud.client.filevisitor.PathCompareVisitor;
import com.zhangyun.filecloud.client.service.PathCompareService;
import com.zhangyun.filecloud.common.exception.StatusException;
import com.zhangyun.filecloud.common.message.CompareResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/27 23:10
 * @since: 1.0
 */
@Component
@Slf4j
@ChannelHandler.Sharable
public class CompareResponseHandler extends SimpleChannelInboundHandler<CompareResponseMessage> {
    @Autowired
    private PathCompareService pathCompareService;
    @Autowired
    private PathCompareVisitor pathCompareVisitor;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CompareResponseMessage responseMessage) throws Exception {
        CompareResponseMessage clientInfo = pathCompareVisitor.getClientInfoList().removeFirst();
        List<String> resultList = pathCompareService.getCompareResultList();
        if (clientInfo == null) {
            throw new StatusException("PathCompareVisitorService状态错误");
        }
        if (!responseMessage.getIsExist()
                || ObjectUtil.notEqual(responseMessage.getType(), clientInfo.getType())) {
            resultList.add(JSONObject.toJSONString(clientInfo) + "=====>" +
                    JSONObject.toJSONString(responseMessage));
        } else if (responseMessage.getType() == CompareResponseMessage.FILE) {
            if (ObjectUtil.notEqual(responseMessage.getLastModified(), clientInfo.getLastModified())
                    || ObjectUtil.notEqual(responseMessage.getMd5(), clientInfo.getMd5())) {
                resultList.add(JSONObject.toJSONString(clientInfo) + "=====>" +
                        JSONObject.toJSONString(responseMessage));
            }
        } else {
            // 比较文件目录 - 将source目录下与target目录对应的文件删除
            // 剩下的target就是source中不存在的文件
            String[] sourceFileList = clientInfo.getFileList();
            List<String> targetFileList = Arrays.stream(responseMessage.getFileList())
                    .filter(targetFile -> {
                        for (String sourceFile : sourceFileList) {
                            if (ObjectUtil.equal(targetFile, sourceFile)) {
                                return false;
                            }
                        }
                        return true;
                    }).collect(Collectors.toList());
            // 输出target中存在但source中不存在的文件或目录
            if (ObjectUtil.isNotEmpty(targetFileList)) {
                resultList.add("{} ====> " + targetFileList);
            }
        }
        pathCompareService.getSemaphore().release();
    }
}
