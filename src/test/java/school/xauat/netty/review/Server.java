package school.xauat.netty.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
 * @date ：Created 2021/11/27 0:32
 * @description：
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        DefaultEventLoopGroup group = new DefaultEventLoopGroup(2);
        Channel channel = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler());
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(group, "default_handler", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("客户端发送数据 -> {}", msg);
                            }
                        });
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(group, "default_handler", new ChannelOutboundHandlerAdapter() {
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                ch.writeAndFlush("abc");
                                super.write(ctx, msg, promise);
                            }
                        });
                    }
                })
                .bind(8080)
                .sync()
                .channel();

    }

}
