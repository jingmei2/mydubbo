package com.magi.registry;

import com.magi.loadbalance.LoadBalance;
import com.magi.loadbalance.RandomLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * @author magi
 */
public class ServiceDiscoveryImpl implements IServiceDiscovery {

    List<String> repos = new ArrayList<>();

    private CuratorFramework curatorFramework;

    public ServiceDiscoveryImpl(){
        //构造函数
        //根据 zookeeper 中的字符串初始化curatorFramework
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZookeeperConfig.CONNECT_STR).sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000,10))
                .build();
        curatorFramework.start();
    }
    /**
     * 根据服务名称得到调用地址
     *
     * @param serviceName
     * @return
     */
    @Override
    public String discovery(String serviceName) {
        // /registrys/com.magi.api.IMjHello
        String path = ZookeeperConfig.ZOOKEEPER_REGISTER_PATH+"/"+serviceName;
        try {
            // /registrys/com.magi.api.IMjHello---->List 127.0.0.1:8080/8081?8082 ---->choose 来选择算法 负载均衡算法
            //根据服务名称,获取底下所有的临时子节点
            repos = curatorFramework.getChildren().forPath(path);
        } catch (Exception e){
            e.printStackTrace();
        }

        //动态感知服务节点的一个变化  /registrys/com.magi.api.IMjHello zk 监听机制
        //监听得到个是个数组
        registerWatch(path);

        //think: 8080 8081 8082 Children ----> impl 选择空间
        //负载均衡 list ---->one
        LoadBalance loadBalance = new RandomLoadBalance();
        return loadBalance.select(repos);
    }

    private void registerWatch(final String path) {
        //获取子节点缓存对象
        //Todo zk源码里有
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework,path,true);
        //获取子节点换成对象监听 重写
        PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                //监听 动态 根据服务名称,获取底下所有的临时子节点 重新赋值
                repos =  curatorFramework.getChildren().forPath(path);
            }
        };
        //添加监听器
        childrenCache.getListenable().addListener(childrenCacheListener);
        try {
            childrenCache.start();
        } catch (Exception e){
            throw new RuntimeException("注册 PathChild Watcher 异常:"+e);
        }
    }
}
