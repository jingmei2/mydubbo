package com.magi.proxy;

import com.magi.bean.RpcRequest;
import com.magi.registry.IServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理
 * @ClassName RpcClientProxy
 * @Description TODO
 * @Author by magi
 * @Date 2018/11/18 17:12
 * @Version 1.0
 **/
public class RpcClientProxy {

    private IServiceDiscovery serviceDiscovery;


    public RpcClientProxy(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    //动态代理的代码
    public  <T> T create(final Class<T> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                //服务发现 url
                String serviceName = interfaceClass.getName();
                String serviceAddress = serviceDiscovery.discovery(serviceName);
                String[] arrs = serviceAddress.split(":");
                String host = arrs[0];
                int port = Integer.parseInt(arrs[1]);

                //url Netty 请求 需要把要调用的内容
                //封装 RPCRequest
                RpcRequest request = new RpcRequest();
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setTypes(method.getParameterTypes());
                request.setParams(args);

                final  RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();

                //发起 Socket 改成了Netty

                //通过 netty 的方式进行连接和发送
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                /**
                                 * This method will be called once the {@link Channel} was registered. After the method returns this instance
                                 * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
                                 *
                                 * @param ch the {@link Channel} which was registered.
                                 * @throws Exception is thrown if an error occurs. In that case it will be handled by
                                 *                   {@link #exceptionCaught(ChannelHandlerContext, Throwable)} which will by default close
                                 *                   the {@link Channel}.
                                 */
                                @Override
                                protected void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline channelPipeline = ch.pipeline();
                                    //拆完之后数据包是一个完整的带有长度域的数据包（之后即可传递到应用层解码器进行解码）
                                    channelPipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4));
                                    channelPipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                                    channelPipeline.addLast("encoder",new ObjectEncoder());
                                    channelPipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE,
                                            ClassResolvers.cacheDisabled(null)));
                                    //使用 netty 写到最后就是写 Handler 的代码
                                    channelPipeline.addLast(rpcProxyHandler);
                                }
                            });

                    //连接服务地址
                    ChannelFuture future = bootstrap.connect(host,port).sync();
                    //将封装好的 request 对象写过去 就想 Socket out.write(request);
                    future.channel().writeAndFlush(request);
                    future.channel().closeFuture().sync();
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    group.shutdownGracefully();
                }
                //服务端写过来的数据
                return rpcProxyHandler.getResponse();
            }
        });
    }
}
