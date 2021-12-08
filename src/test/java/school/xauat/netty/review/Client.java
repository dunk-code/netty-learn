package school.xauat.netty.review;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @author ：zsy
 * @date ：Created 2021/11/27 0:40
 * @description：
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        DefaultEventLoopGroup default_group = new DefaultEventLoopGroup();
        Channel channel = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(default_group, "default_handler", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("服务端发送的数据 -> {}", msg);
                            }
                        });
                    }
                })
                .connect("localhost", 8080)
                .sync()
                .channel();

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
        }).start();
        ChannelFuture channelFuture = channel.closeFuture();
        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                group.shutdownGracefully();
            }
        });
    }

}
