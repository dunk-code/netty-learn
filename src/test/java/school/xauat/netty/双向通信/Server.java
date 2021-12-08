package school.xauat.netty.双向通信;

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
 * @date ：Created 2021/11/30 22:45
 * @description：服务端代码
 */
@Slf4j
public class Server {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                System.out.println(buf.toString(Charset.defaultCharset()));

                                // 建议使用ctx.alloc创建ByteBuffer
                                ByteBuf response = ctx.alloc().buffer();
                                response.writeBytes(buf);
                                ctx.writeAndFlush(response);

                                // 释放资源
                                buf.release();
                                response.release();
                            }
                        });
                    }
                })
                .bind(8080);

    }

}
