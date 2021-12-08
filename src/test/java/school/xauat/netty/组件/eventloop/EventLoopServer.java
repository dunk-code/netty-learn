package school.xauat.netty.组件.eventloop;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @author ：zsy
 * @date ：Created 2021/11/24 16:17
 * @description：
 */
@Slf4j
public class EventLoopServer {

    public static void main(String[] args) throws InterruptedException {
        // 拆分EventLoopGroup boss worker
        // boss负责建立连接， worker负责读写
        NioEventLoopGroup boss = new NioEventLoopGroup();   // 不需要设置成1个，默认一个服务端
        NioEventLoopGroup worker = new NioEventLoopGroup(2);
        // 创建两个非NIOEventLoopGroup 负责事件处理
        DefaultEventLoopGroup group = new DefaultEventLoopGroup(2);
        new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast("nio_handler", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                log.debug(buf.toString(Charset.defaultCharset()));
                                ctx.fireChannelRead(msg); // 将消息传递给下一个handler
                            }
                        })
                                // 创建一个DefaultEventLoopGroup对象进行处理
                                .addLast(group, "default_handler", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        ByteBuf buf = (ByteBuf) msg;
                                        log.debug(buf.toString(Charset.defaultCharset()));
                                    }
                                });
                    }
                })
                .bind(8080).sync();
    }
}
