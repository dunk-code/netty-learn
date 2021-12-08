package school.xauat.netty.review;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @author ：zsy
 * @date ：Created 2021/11/24 13:59
 * @description：
 */
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        // 启动器
        new Bootstrap()
                // 添加EventLoop
                .group(new NioEventLoopGroup())
                // 选择客户端channel
                .channel(NioSocketChannel.class)
                // 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel()
                .writeAndFlush("Hello Netty");

    }
}
