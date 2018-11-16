package registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class RegisterCenterImpl implements IRegisterCenter {

    private CuratorFramework curatorFramework;
    {
        //根据 zookeeper中的字符串初始化 curatorFramework
        //先连接 zookeeper
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZookeeperConfig.CONNECT_STR)
                .sessionTimeoutMs(4000).retryPolicy(
                new ExponentialBackoffRetry(1000,10)
        ).build();
        //记得先开启
        curatorFramework.start();

    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        //代码和zk需要打交道 zkjar 操作api zkclient  zkcurator
        //增加节点(根目录)
        // /registerys/com.magi.IMjHello
        String servicePath = (ZookeeperConfig.ZOOKEEPER_REGISTER_PATH+"/"+serviceName).replaceAll("/+","/");
        try {
            //判断 注册节点 /registerys/iMjHello 是否存在,不存在则创建
            if (curatorFramework.checkExists().forPath(servicePath)==null){
                // 不存在的话,就永久创建 /registerys/iMjHello
                curatorFramework.create()
                        .creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath,"123".getBytes());
            }
            //然后获取节点下的 子节点
            String addressPath = servicePath + "/" + serviceAddress;
            //临时节点
            String reNode = curatorFramework.create()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(addressPath,"123".getBytes());
            System.out.println("服务注册成功:"+reNode);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            //关闭curator 工厂
            curatorFramework.close();
        }
    }
}
