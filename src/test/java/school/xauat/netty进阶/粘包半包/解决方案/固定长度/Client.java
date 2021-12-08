package school.xauat.netty进阶.粘包半包.解决方案.固定长度;

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

    private static byte[] fill0bytes(char c, int len, int fixedLen) {
        if (len > fixedLen) {
            throw new RuntimeException("len:" + len + "超过fixedLen:" + fixedLen);
        }
        StringBuilder builder = new StringBuilder();
        byte[] bytes = new byte[fixedLen];
        int index = 0;
        while (index < len) {
            bytes[index] = (byte) c;
            builder.append(c);
            index++;
        }
        while (index < fixedLen) {
            bytes[index] = '_';
            builder.append('_');
            index++;
        }
        System.out.println(builder.toString());
        return bytes;
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

                                    // 向服务端发送数据
                                    Random random = new Random();
                                    ByteBuf buffer = ctx.alloc().buffer(16);
                                    for (int i = 0; i < 10; i++) {
                                        buffer.writeBytes(fill0bytes((char) (i + '0'), random.nextInt(10) + 1, 10));
                                    }
                                    ctx.channel().writeAndFlush(buffer);
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
