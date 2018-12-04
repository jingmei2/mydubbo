package com.magi;

import com.magi.api.IMjHello;
import com.magi.proxy.RpcClientProxy;
import com.magi.registry.IServiceDiscovery;
import com.magi.registry.ServiceDiscoveryImpl;

/**
 * @author magi
 */
public class ClientTest {
    public static void main(String[] args) {
        //无法 本地调用
        //IMjHello iMjHello = new IMjHelloImpl();
        //发起远程调用
        //远程调用 动态代理类
        //url 服务发现
        IServiceDiscovery iServiceDiscovery = new ServiceDiscoveryImpl();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(iServiceDiscovery);
        IMjHello iMjHello = rpcClientProxy.create(IMjHello.class);
        System.out.println(iMjHello.sayHello("Majing"));
    }
}
