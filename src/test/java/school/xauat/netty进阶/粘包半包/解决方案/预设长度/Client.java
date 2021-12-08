package school.xauat.netty进阶.粘包半包.解决方案.预设长度;

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

    private static void decorate_buffer(ByteBuf buffer, String content) {
        int length = content.length();
        buffer.writeInt(length);
        // 写入版本号
        buffer.writeByte(1);
        buffer.writeBytes(content.getBytes());
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
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    decorate_buffer(buffer, "Hello, Netty");
                                    decorate_buffer(buffer, "I'm zhangsan");
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
