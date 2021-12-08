package school.xauat.nio.网络编程.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author ：zsy
 * @date ：Created 2021/11/22 0:14
 * @description：
 */
public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT, null);

        while (true) {
            selector.select();

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ, null);

                    // 向客户端发送数据
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        builder.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(builder.toString());
                    int write = sc.write(buffer);
                    System.out.println("实际写了多少字节：" + write);

                    // 如果剩余字节没有写完，需要注册事件
                    if (buffer.hasRemaining()) {
                        // 在原有读事件的基础上注册写事件
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);

                        // 将buffer作为附件加到scKey中
                        scKey.attach(buffer);
                    }
                } else if(key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println("实际写了多少字节：" + write);
                    if (!buffer.hasRemaining()) {
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        // 删除缓冲区
                        key.attach(null);
                    }
                }
            }
        }
    }
}
