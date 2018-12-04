package registry;

/**
 * @author magi
 */
public interface IRegisterCenter {

    /**
     * serviceName: com.magi.IMjHello
     * serviceAddress: 127.0.0.1:8080
     * 将 serviceName 与 serviceAddress 绑定在一起 注册中心 zookeeper 上
     * @param serviceName
     * @param serviceAddress
     */
    void register(String serviceName,String serviceAddress);
}
