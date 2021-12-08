package school.xauat.netty进阶.粘包半包.解决方案.固定分割符;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author ：zsy
 * @date ：Created 2021/12/1 15:48
 * @description：
 */
@Slf4j
public class Client {

    private static byte[] input_handler(char c, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            builder.append(c);
        }
        builder.append('\n');
        return builder.toString().getBytes();
    }

    private void send() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("connected...{}", ctx.channel());
                                    Random random = new Random();
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    for (int i = 0; i < 10; i++) {
                                        byte[] bytes = input_handler((char) ('a' + i), random.nextInt(100) + 1);
                                        buffer.writeBytes(bytes);
                                    }
                                    ctx.writeAndFlush(buffer);
                                }
                            });
                        }
                    })
                    .connect("localhost", 8080);
            channelFuture.sync();
            channelFuture.channel().closeFuture().addListener(future -> {
                group.shutdownGracefully();
                log.debug("stoped..");
            });
        } catch (InterruptedException e) {
            log.error("client error", e);
        }
    }

    public static void main(String[] args) {
        new Client().send();
    }
}
