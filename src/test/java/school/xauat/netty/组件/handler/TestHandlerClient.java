package school.xauat.netty.组件.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @author ：zsy
 * @date ：Created 2021/11/29 13:26
 * @description：
 */
@Slf4j
public class TestHandlerClient {

    public static void main(String[] args) throws InterruptedException {
        Channel channel = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 8080)
                .sync()
                .channel();
        new Thread(() -> {
            Scanner in = new Scanner(System.in);
            while (true) {
                String line = in.nextLine();
                if (line.equals("q")) {
                    channel.close();
                    break;
                }
                channel.writeAndFlush(line);
            }
        }, "son").start();

        ChannelFuture channelFuture = channel.closeFuture();
        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                log.debug("chanel关闭->{}, 执行其余操作", channel);
            }
        });
    }

}
