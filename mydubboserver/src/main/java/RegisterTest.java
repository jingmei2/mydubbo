import registry.IRegisterCenter;
import registry.RegisterCenterImpl;

/**
 * 测试类 往注册中心,注册服务
 * @author magi
 */
public class RegisterTest {
    public static void main(String[] args) {
        IRegisterCenter registerCenter = new RegisterCenterImpl();
        registerCenter.register("com.magi.Product","127.0.0.1:8080");
        //System.in.read();
    }
}
