package school.xauat.聊天业务.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.RpcRequestMessage;
import school.xauat.聊天业务.message.RpcResponseMessage;
import school.xauat.聊天业务.server.service.HelloService;
import school.xauat.聊天业务.server.service.ServicesFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author ：zsy
 * @date ：Created 2021/12/5 20:27
 * @description：
 */
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) {
        RpcResponseMessage responseMessage = new RpcResponseMessage();
        try {
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(message.getInterface_name()));
            Method method = service.getClass().getMethod(message.getMethod_name(), message.getParameter_types());
            Object invoke = method.invoke(service, message.getParameter_values());
            responseMessage.setSequenceId(message.getSequenceId());
            responseMessage.setReturn_value(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getCause().getCause().getMessage();
            responseMessage.setException_value(new Exception("远程调用出错:" + msg));
        }
        ctx.channel().writeAndFlush(responseMessage);
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "school.xauat.聊天业务.server.service.HelloService",
                "helloService",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"});
        HelloService service = (HelloService) ServicesFactory.getService(Class.forName(message.getInterface_name()));
        System.out.println(service);
        Method method = service.getClass().getMethod(message.getMethod_name(), message.getParameter_types());
        Object invoke = method.invoke(service, message.getParameter_values());
        System.out.println(invoke);
    }
}
