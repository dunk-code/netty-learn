package school.xauat.netty.双向通信;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author ：zsy
 * @date ：Created 2021/11/30 23:50
 * @description：客户端代码
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel ch = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // pipeline.addLast(new LoggingHandler());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                System.out.println(buf.toString(Charset.defaultCharset()));

                                // 释放资源
                                buf.release();
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
                    ch.close();
                    break;
                }
                ch.writeAndFlush(next);
            }
        }).start();

        ChannelFuture channelFuture = ch.closeFuture();
        channelFuture.addListener(future -> {
            group.shutdownGracefully();
            log.debug("执行关闭后操作");
        });
    }

}
