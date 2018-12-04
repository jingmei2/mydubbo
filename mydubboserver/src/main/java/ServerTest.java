import com.magi.api.IMjHello;
import com.magi.api.MjHelloImpl;
import com.magi.rpc.RpcService;
import registry.IRegisterCenter;
import registry.RegisterCenterImpl;

public class ServerTest {
    public static void main(String[] args) {
        //最终要给客户端使用的对象
        IMjHello iMjHello = new MjHelloImpl();
        //把服务注册到注册中心上
        IRegisterCenter registerCenter = new RegisterCenterImpl();

        //registerCenter.register("com.magi.IMjHello","127.0.0.1");
        //写法 注解方式 也就是根据子类可以得到接口的名称


        //绑定---->把接口的子类对象进行绑定 RPCService
        RpcService rpcService = new RpcService(registerCenter,"127.0.0.1:8080");
        rpcService.bind(iMjHello);
        rpcService.publisher();

        //注册服务
        //


        //TODO netty 进行端口监听
    }
}
