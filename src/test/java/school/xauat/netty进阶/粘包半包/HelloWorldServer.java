package school.xauat.netty进阶.粘包半包;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：zsy
 * @date ：Created 2021/12/1 12:15
 * @description：
 */
@Slf4j
public class HelloWorldServer {

     void start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    // 设置接收缓冲区大小
                    .option(ChannelOption.SO_RCVBUF, 10)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("connected..->{}", ctx.channel());
                                    super.channelActive(ctx);
                                }

                                @Override
                                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("disconnect..->{}", ctx.channel());
                                    super.channelInactive(ctx);
                                }
                            });
                        }
                    })
                    .bind(8080);
            channelFuture.sync();
            channelFuture.channel().closeFuture().addListener(future -> {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
                log.debug("stoped..");
            });
        } catch (InterruptedException e) {
            log.error("server error", e);
        }
    }

    public static void main(String[] args) {
        new HelloWorldServer().start();
    }

}
