package com.zhangyun.filecloud.common.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/7/31 16:28
 * @since: 1.0
 */
public class FrameDecoder extends LengthFieldBasedFrameDecoder {

    public FrameDecoder() {
        this(2000000, 32, 4, 0, 0);
    }

    public FrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
