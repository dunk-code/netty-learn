package school.xauat.netty.组件.channel;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import java.util.Scanner;

/**
 * @author ：zsy
 * @date ：Created 2021/11/25 13:58
 * @description：
 */
@Slf4j
public class CloseFutureClient {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("{}", msg);
                            }
                        });
                    }
                })
                .connect("localhost", 8080);
        channelFuture.sync();
        Channel channel = channelFuture.channel();
        new Thread(() -> {
            Scanner in = new Scanner(System.in);
            while (true) {
                String next = in.nextLine();
                if (next.equals("q")) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(next);
            }
        }, "input").start();
        // 获取CloseFuture对象
        ChannelFuture closeFuture = channel.closeFuture();

        // 同步处理
        /*System.out.println("main");
        closeFuture.sync();
        log.debug("处理关闭后操作");*/

        // 异步处理
        closeFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.debug("处理关闭后操作");
                group.shutdownGracefully();
            }
        });
    }

}
