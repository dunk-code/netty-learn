package school.xauat.nio.review;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static school.xauat.util.ByteBufferUtil.debugRead;

/**
 * @author ：zsy
 * @date ：Created 2021/11/21 22:59
 * @description：
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        // 创建多路复用器
        Selector selector = Selector.open();

        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 设置非阻塞
        ssc.configureBlocking(false);

        // 向selector中注册服务器
        SelectionKey sscKey = ssc.register(selector, 0, null);

        log.debug("sscKey -> {}", sscKey);
        // 设置关注事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);

        // 绑定端口
        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            // 监听事件，如果没有事件，线程阻塞，有事件，线程恢复
            selector.select();

            // 获取selectKeys集合迭代器
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey sKey = iterator.next();

                // 手动删除sKey
                iterator.remove();

                log.debug("sKey -> {}", sKey);

                if (sKey.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) sKey.channel();

                    // 获取对应客户端channel
                    SocketChannel sc = channel.accept();
                    log.debug("sc -> {}", sc);

                    // 将channel注册到Selector中
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    scKey.interestOps(SelectionKey.OP_READ);
                    log.debug("scKey -> {}", scKey);
                } else if (sKey.isReadable()) {
                    try {
                        SocketChannel sc = (SocketChannel) sKey.channel();

                        // 获取附件
                        ByteBuffer buffer = (ByteBuffer) sKey.attachment();

                        int read = sc.read(buffer);
                        if (read > 0) {

                            // 读取channel中的数据
                            split(buffer);

                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                sKey.attach(newBuffer);
                            }
                        } else {
                            sKey.cancel();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
                debugRead(tmp);
            }
        }

        buffer.compact();
    }
}
