package school.xauat.netty.helloworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

/**
 * @author ：zsy
 * @date ：Created 2021/11/23 23:10
 * @description：
 */
public class HelloServer {

    public static void main(String[] args) {
        // 启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()

                // NioEventLoopGroup 相当于之前的boss和worker是(selector, thread)，group组
                .group(new NioEventLoopGroup())

                // 服务器的 ServerSocketChannel实现
                .channel(NioServerSocketChannel.class) // OIO(BIO) NIO epoll

                // boss负责处理连接 worker负责处理读写事件 childHandler决定了worker能够执行那些操作（handler）
                .childHandler(

                        // channel代表和客户端进行数据读写的通道SocketChannel，ChannelInitializer初始化器 负责添加别的handler
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                // 添加具体的handler

                                // 解码 ByteBuf -> String
                                nioSocketChannel.pipeline().addLast(new StringDecoder());
                                // 自定义handler
                                nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                    // 处理读事件
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        // 打印字符串
                                        System.out.println(msg);
                                    }
                                });
                            }
                        }
                )
                .bind(new InetSocketAddress(8080));
    }
}
