package com.magi.rpc;

import com.magi.bean.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RpcServerHandler
 * @Description TODO
 * @Author by magi
 * @Date 2018/11/18 00:20
 * @Version 1.0
 **/
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 储存service 对象和 url 的对应关系
     */
    private Map<String,Object> handleMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> handleMap) {
        this.handleMap = handleMap;
    }

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
        //ctx 写   msg 读
        RpcRequest rpcRequest = (RpcRequest) msg;

         Object result = new Object();
         if (handleMap.containsKey(rpcRequest.getClassName())){
             //调用 map 中的子类对象进行执行
             //Method method = handleMap.get(rpcRequest.getClassName()).getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypes());
             Object clazz = handleMap.get(rpcRequest.getClassName());
             Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypes());
             method.invoke(clazz,rpcRequest.getParams());
         }
         ctx.write(result);
         ctx.flush();
         ctx.close();
        //super.channelRead(ctx, msg);
    }
}
