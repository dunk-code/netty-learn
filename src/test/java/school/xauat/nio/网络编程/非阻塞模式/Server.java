package school.xauat.nio.网络编程.非阻塞模式;

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
        ByteBuffer buffer = ByteBuffer.allocate(16);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(8080));
        // 设置为非阻塞模式，此时accept()方法将不会阻塞
        ssc.configureBlocking(false);
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // log.debug("connecting...");
            SocketChannel curChannel = ssc.accept();
            if (curChannel != null) {
                log.debug("connect->{}", curChannel);
                // 设置为非阻塞状态，此时read()方法将不会阻塞
                curChannel.configureBlocking(false);
                channels.add(curChannel);
            }
            for (SocketChannel channel : channels) {
                int read = channel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}", channel);
                }
            }
        }

    }

}
