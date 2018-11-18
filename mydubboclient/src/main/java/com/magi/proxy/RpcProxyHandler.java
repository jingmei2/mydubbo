package com.magi.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName RpcProxyHandler
 * @Description TODO
 * @Author by magi
 * @Date 2018/11/18 17:39
 * @Version 1.0
 **/
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

    /**
     * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ctx 写 msg 读

        //super.channelRead(ctx, msg);
    }
}
