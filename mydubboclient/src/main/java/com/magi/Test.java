package com.magi;

import com.magi.registry.IServiceDiscovery;
import com.magi.registry.ServiceDiscoveryImpl;
import com.magi.registry.ZookeeperConfig;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        // 127.0.0.1:8080/8081/8082
        IServiceDiscovery serviceDiscovery = new ServiceDiscoveryImpl();
        System.out.println(serviceDiscovery.discovery("com.magi.api.IMjHello"));
        System.in.read();
    }
}
