package school.xauat.netty.helloworld;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @author ：zsy
 * @date ：Created 2021/11/23 23:39
 * @description：
 */
public class HelloClient {

    public static void main(String[] args) throws InterruptedException {
        // 启动器
        new Bootstrap()
                // 添加EventLoopGroup
                .group(new NioEventLoopGroup())
                // 选择客户端channel实现
                .channel(NioSocketChannel.class)
                // 添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //添加编码器
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                // 连接服务器
                .connect(new InetSocketAddress("localhost", 8080))
                .sync()
                .channel()
                // 发送数据
                .writeAndFlush("Hello Netty");
    }
}
