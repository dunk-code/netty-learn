package school.xauat.nio.网络编程.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author ：zsy
 * @date ：Created 2021/11/22 0:20
 * @description：
 */
@Slf4j
public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);

        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT, null);
        sc.connect(new InetSocketAddress("localhost", 8080));

        int count = 0;
        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    if (sc.finishConnect()) {
                        log.debug("connect -> {}", sc);
                        key.interestOps(key.interestOps() + SelectionKey.OP_READ);
                    }
                } else if (key.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                    count += sc.read(buffer);
                    buffer.clear();
                    System.out.println(count);
                    if (!buffer.hasRemaining()) {
                        key.interestOps(key.interestOps() - SelectionKey.OP_READ);
                    }
                }

            }
        }
    }
}
