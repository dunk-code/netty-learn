package school.xauat.nio.review;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author ：zsy
 * @date ：Created 2021/11/22 14:00
 * @description：
 */
public class WriteClient {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress("localhost", 8080));

        Selector selector = Selector.open();
        sc.register(selector, SelectionKey.OP_CONNECT, null);

        int count = 0;
        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isConnectable()) {
                    if (sc.finishConnect()) {
                        key.interestOps(key.interestOps() + SelectionKey.OP_READ);
                        sc.write(Charset.defaultCharset().encode("Hello\nWorld-Netty-Zhang-San\n"));
                    } else {
                        key.cancel();
                    }
                } else if (key.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                    count += sc.read(buffer);
                    System.out.println(count);
                    if (!buffer.hasRemaining()) {
                        key.interestOps(key.interestOps() - SelectionKey.OP_READ);
                    }
                }
            }
        }
    }

}
