package school.xauat.nio.网络编程.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

/**
 * @author ：zsy
 * @date ：Created 2021/11/21 16:00
 * @description：
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        // 创建Selector
        Selector selector = Selector.open();

        // 创建服务端通道，设置为非阻塞
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 将服务端通道注册到Selector中
        // SelectionKey就是事件发生后，通过它可以知道事件和那个channel的事件(客户端channel)
        SelectionKey sscKey = ssc.register(selector, 0, null);

        // 设置关注事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT); // 四种事件：accept、connect、read、write

        log.debug("register key -> {}", sscKey);

        // 绑定端口
        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            // select()方法没有事件发生，线程阻塞，有事件发生，线程恢复运行
            // 在事件未处理是不会阻塞，事件发生后要么处理，要么取消
            selector.select();

            // 从selector中获取事件发生的所有SelectionKeys
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            // 通过迭代器遍历
            while (iterator.hasNext()) {
                SelectionKey sKey = iterator.next();
                log.debug("sKey -> {}", sKey);
                // 手动删除selectedKeys中的当前selectionKey
                iterator.remove();

                if (sKey.isAcceptable()) { // 如果是一个accept事件
                    // 获取对应channel
                    ServerSocketChannel channel = (ServerSocketChannel) sKey.channel();
                    // log.debug("serverSocketChannel -> {}", channel);
                    SocketChannel sc = channel.accept();
                    log.debug("socketChannel -> {}", sc);

                    // 注册当前socketChannel
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16);

                    // attachment 附件，绑定当前selectKey
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("scKey -> {}", scKey);
                } else if (sKey.isReadable()) { // 如果是一个读事件
                    try {
                        SocketChannel sc = (SocketChannel) sKey.channel();
                        ByteBuffer buffer = (ByteBuffer) sKey.attachment();
                        int read = sc.read(buffer);
                        if (read > 0) {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                sKey.attach(newBuffer);
                            }
                        } else {
                            // 因为关闭连接也是一个读时间
                            // 如果客户端主动关闭，需要手动取消事件
                            sKey.cancel();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        // 如果客户端强制关闭，则会触发异常，需要手动取消事件
                        sKey.cancel();
                    }
                }
            }
        }

    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                int len = i + 1 - buffer.position();
                ByteBuffer tmp = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    tmp.put(buffer.get());
                }
                tmp.flip();
                // debugRead(tmp);
                System.out.println(Charset.defaultCharset().decode(tmp));
            }
        }
        buffer.compact();
    }

}
