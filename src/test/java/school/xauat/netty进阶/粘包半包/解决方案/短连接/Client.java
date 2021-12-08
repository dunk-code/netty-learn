package school.xauat.netty进阶.粘包半包.解决方案.短连接;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ：zsy
 * @date ：Created 2021/12/1 15:20
 * @description：
 */
@Slf4j
public class Client {
    private void send() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    log.debug("connected...{}", ctx.channel());

                                    // 向服务端发送数据
                                    ByteBuf buffer = ctx.alloc().buffer(16);
                                    buffer.writeBytes(new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15});
                                    ctx.channel().writeAndFlush(buffer);
                                    ctx.close();
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
        for (int i = 0; i < 10; i++) {
            new Client().send();
        }
    }
}
