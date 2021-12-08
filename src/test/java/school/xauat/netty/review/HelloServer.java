package school.xauat.netty.review;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;

/**
 * @author ：zsy
 * @date ：Created 2021/11/24 13:53
 * @description：
 */
public class HelloServer {

    public static void main(String[] args) {

        // 创建启动器
        new ServerBootstrap()
                // 添加EventLoopGroup组
                .group(new NioEventLoopGroup())
                // 选择服务端Channel实现
                .channel(NioServerSocketChannel.class)
                // 绑定子类处理器
                .childHandler(
                        // 初始化器
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) throws Exception {
                                // 添加编码器
                                ch.pipeline().addLast(new StringDecoder());
                                // 添加自定义handler
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    // 监听read事件
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                        }
                )
                // 绑定端口
                .bind(new InetSocketAddress(8080));
    }
}
