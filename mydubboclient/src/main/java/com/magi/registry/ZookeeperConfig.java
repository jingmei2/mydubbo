package com.magi.registry;

/**
 * @author magi
 */
public class ZookeeperConfig {
    //集群的地址和端口号
    public final static String CONNECT_STR = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    //服务名称和 URL 地址的
    public final static String ZOOKEEPER_REGISTER_PATH = "/registerys";
}
