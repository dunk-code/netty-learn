package school.xauat.netty.review;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @author ：zsy
 * @date ：Created 2021/11/26 18:32
 * @description：
 */
@Slf4j
public class CloseFutureClient {

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 8080);
        Channel channel = channelFuture.sync().channel();
        new Thread(() -> {
            Scanner in = new Scanner(System.in);
            while (true) {
                String next = in.nextLine();
                if (next.equals("q")) {
                    channel.close();
                    log.debug("channel is close -> {}", channel);
                    break;
                }
                channel.writeAndFlush(next);
            }
        }, "input").start();
    }

}
