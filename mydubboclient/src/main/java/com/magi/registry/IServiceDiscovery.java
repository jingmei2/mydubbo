package com.magi.registry;

/**
 * @author magi
 */
public interface IServiceDiscovery {
    /**
     * 根据服务名称得到调用地址
     * @param serviceName
     * @return
     */
    String discovery(String serviceName);
}
