package school.xauat.nio.网络编程.阻塞模式;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static school.xauat.util.ByteBufferUtil.debugRead;

/**
 * @author ：zsy
 * @date ：Created 2021/11/21 11:03
 * @description： 阻塞模式（单线程）
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        // 创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 绑定端口号
        ssc.bind(new InetSocketAddress(8080));

        // 创建连接集合
        List<SocketChannel> channels = new ArrayList<>();

        // 循环接受多个客户端发送的数据
        while (true) {

            // accept用来和客户端建立连接，SocketChannel用来与客户端通信
            log.debug("connecting...");
            SocketChannel curChannel = ssc.accept(); // 阻塞方法，线程停止运行
            log.debug("connected->{}", curChannel);
            channels.add(curChannel);

            // 循环读取每个channel中的数据
            for (SocketChannel channel : channels) {
                log.debug("before read->{}", channel);
                channel.read(buffer); // 阻塞方法，线程停止运行
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.debug("after read->{}", channel);
            }
        }
    }

}
