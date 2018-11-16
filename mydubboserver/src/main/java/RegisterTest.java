import registry.IRegisterCenter;
import registry.RegisterCenterImpl;

import java.io.IOException;

/**
 * 测试类 往注册中心,注册服务
 * @author magi
 */
public class RegisterTest {
    public static void main(String[] args) throws IOException {
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        registerCenter.register("com.magi.Abc","127.0.0.1:2181");
        System.in.read();
        // 这里有个小技巧，让main程序一直监听控制台输入，异步的代码就可以一直在执行。不同于while(ture)的是，按回车或esc可退出     
    }
}
