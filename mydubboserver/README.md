# dubbo 实现代码
RPC 核心:
(1)传输过程
(2)序列化和反序列化

dubbo:RPC 的一种

需求:
两端: provider[商品模块. xxx 类方法]
     consumer[用户模块 调用 server 的服务]
     
1 把 provider端的服务写到 zookeeper上 服务注册
  (1)java 操作zookeeper api
  (2)java 代码连接上 zk
  (3)serviceName,url----->zk,节点和子节点上
  (4)服务名称注册成永久性的节点,和暂时性的子节点

2 consumer 要进行服务发现 根据服务名称发现 url 地址
  (1)服务发现
  (2)监听 watcher
  (3)负载均衡算法 随机