package school.xauat.聊天业务.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import school.xauat.聊天业务.client.handler.RpcResponseMessageHandler;
import school.xauat.聊天业务.message.RpcRequestMessage;
import school.xauat.聊天业务.protocol.MessageCodecSharable;
import school.xauat.聊天业务.protocol.ProcotolFrameDecoder;
import school.xauat.聊天业务.protocol.SequenceIdGenerator;
import school.xauat.聊天业务.server.service.HelloService;

import java.lang.reflect.Proxy;

import static school.xauat.聊天业务.client.handler.RpcResponseMessageHandler.PROMISES;

/**
 * @author ：zsy
 * @date ：Created 2021/12/5 21:25
 * @description：
 */
@Slf4j
public class RpcClient {

    public static void main(String[] args) {
        HelloService helloService = getProxyServices(HelloService.class);
        System.out.println(helloService.helloService("张三"));
        System.out.println(helloService.helloService("李四"));
    }

    private static <T> T getProxyServices(Class<T> services) {
        ClassLoader loader = services.getClassLoader();
        Class<?>[] interfaces = new Class[]{services};
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            int sequenceId = SequenceIdGenerator.getSequenceId();
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    services.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            getChannel().writeAndFlush(message);
            // 准备一个空的promise用来接收请求的返回结果
            // getChannel().eventLoop()指定promise对象异步接收结果的对象
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            PROMISES.put(sequenceId, promise);

            // 同步等待线程的执行结果
            promise.await();

            if (promise.isSuccess()) {
                return promise.getNow();
            } else {
                return new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }

    private static Channel channel = null;
    private static final Object LOCK = new Object();

    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        LoggingHandler LOG_HANDLER = new LoggingHandler();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        ChannelFuture future = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProcotolFrameDecoder());
                        ch.pipeline().addLast(LOG_HANDLER);
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(RPC_HANDLER);
                    }
                })
                .connect("localhost", 8080);
        try {
            future.sync();
            channel = future.channel();
            future.channel().closeFuture().addListener(future1 -> {
                group.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
