package com.magi.rpc;

import com.magi.bean.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import registry.IRegisterCenter;
import registry.RegisterCenterImpl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RpcServerHandler
 * @Description TODO
 * @Author by magi
 * @Date 2018/11/18 00:20
 * @Version 1.0
 **/
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 储存service 对象和 url 的对应关系
     */
    private Map<String,Object> handleMap = new HashMap<>();

    public RpcServerHandler(Map<String, Object> handleMap) {
        this.handleMap = handleMap;
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ctx 写   msg 读
        RpcRequest rpcRequest = (RpcRequest) msg;

         Object result = new Object();
         if (handleMap.containsKey(rpcRequest.getClassName())){
             //调用 map 中的子类对象进行执行
             //Method method = handleMap.get(rpcRequest.getClassName()).getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypes());
             Object clazz = handleMap.get(rpcRequest.getClassName());
             Method method = clazz.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getTypes());
             method.invoke(clazz,rpcRequest.getParams());
         }
         ctx.write(result);
         ctx.flush();
         ctx.close();
        //super.channelRead(ctx, msg);
    }

    public static void main(String[] args) throws IOException {

        System.out.println(maximumSwap(931604));
    }

    public static int maximumSwap(int num) {
        // Write your code here

        // Write your code here
        //先将整数转成字符数组，方便取出所有数字
        String str = String.valueOf(num);
        char[] array = str.toCharArray();
        char max = 0;
        char second = 0;
        int len = array.length;
        //先循环得到最大的数字,和第二大的数字
        for (int i = 0; i < len; i++) {
            char temp = 0;
            if (max<array[i]){
                temp = max;
                max = array[i];
                second = temp;
            }
            second = temp;


        }
        //如果第一个最高位已经是最大的了，那就要交换第二大的数字
        //指向最大的数字的位置
        int pos = 0;
        //最大的数在第一个还是第二个的标识
        int flag = 0;

        char temp = max;
        if (max==array[0]&&len>1){
            temp = second;
            flag = 1;
        }
        //然后循环出最大的数字的位置
        for (int i = flag; i < len; i++) {
            if (temp == array[i]){
                pos = i;
                break;
            }
        }
        //位置所在数字 和第一个数进行交换 或者和第二个数进行交换
        char tempNum = array[flag];
        array[flag] = array[pos];
        array[pos] = tempNum;

        return Integer.parseInt(String.valueOf(array));

    }
}
