package com.magi.rpc;

import com.magi.annotation.RpcAnnotation;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import registry.IRegisterCenter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author magi
 */
public class RpcService {

    private IRegisterCenter registerCenter;
    private String serviceAddress;

    /**
     * 储存service 对象和 url 的对应关系
     */
    private Map<String,Object> handleMap = new HashMap<>();

    public RpcService(IRegisterCenter registerCenter, String serviceAddress) {
        this.registerCenter = registerCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * 无论传多少个 service 都可以处理
     * 把服务名称和子类对象一一对应
     * @param services
     */
    public void  bind(Object... services) {
        for (Object service :services){
            //得到服务名称
            RpcAnnotation rpcAnnotation = service.getClass().getAnnotation(RpcAnnotation.class);
            // com.magi.IMjHello
            String serviceName = rpcAnnotation.value().getName();
            //最终客户端要根据服务名称调用对应的子类对象实现
            handleMap.put(serviceName,service);
        }
    }

    /**
     * 发布服务
     */
    public void publisher() {
        for( String serviceName: handleMap.keySet()){
            //循环把服务发布到注册中心
            registerCenter.register(serviceName,serviceAddress);
        }

        //启动一个 Netty 监听
        try {
            // 接收连接,但是不处理
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            // 真正处理连接的group
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            //加载Initializer
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //这一步是必须的，如果没有设置group将会报java.lang.IllegalStateException: group not set异常
            serverBootstrap = serverBootstrap.group(bossGroup, workerGroup);
            //serverBootstrap.group(bossGroup, workerGroup);

            //装载 Socket
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.childHandler(
                new ChannelInitializer<SocketChannel>() {
                    //既然是通信，那当然有通道，Channel就是通道
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        //基于长度的拆包
                        //拆完之后数据包是一个完整的带有长度域的数据包（之后即可传递到应用层解码器进行解码）
                        channelPipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4));
                        channelPipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
                        channelPipeline.addLast("encoder",new ObjectEncoder());
                        channelPipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE,
                                ClassResolvers.cacheDisabled(null)));

                        //自定义的 Handler Netty 用到最后,就是写一个个 Handler 就像SpringMVC Handler 读写
                        channelPipeline.addLast(new RpcServerHandler(handleMap));
                    }
                    //* option()是提供给NioServerSocketChannel用来接收进来的连接。
                    //* childOption()是提供给由父管道ServerChannel接收到的连接，
                    //* 在这个例子中也是NioServerSocketChannel。
                }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
            //通过 netty 进行监听 8080
            String[] addrs = serviceAddress.split(":");
            String ip = addrs[0];
            int port = Integer.parseInt(addrs[1]);
            //绑定端口并启动去接收进来的连接
            ChannelFuture future = serverBootstrap.bind(ip,port).sync();
            System.out.println("netty 服务端启动成功,等待客户端的链接:");
            //这里会一直等待，直到socket被关闭
            future.channel().closeFuture().sync();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    //绑定


    //注册服务
}
