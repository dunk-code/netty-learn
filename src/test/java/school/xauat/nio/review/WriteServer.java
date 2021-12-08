package school.xauat.nio.review;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author ：zsy
 * @date ：Created 2021/11/22 13:47
 * @description：
 */
public class WriteServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));

        Selector select = Selector.open();
        ssc.register(select, SelectionKey.OP_ACCEPT, null);

        while (true) {
            select.select();
            Iterator<SelectionKey> iterator = select.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    Map<String, ByteBuffer> map = new HashMap<>();
                    map.put("read", ByteBuffer.allocate(4));
                    SelectionKey scKey = sc.register(select, SelectionKey.OP_READ, map);

                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        builder.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(builder.toString());

                    int write = sc.write(buffer);
                    System.out.println("实际写了多少字节：" + write);

                    if (buffer.hasRemaining()) {
                        scKey.interestOps(scKey.interestOps() + SelectionKey.OP_WRITE);
                        map.put("write", buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) ((HashMap) key.attachment()).get("write");
                    SocketChannel sc = (SocketChannel) key.channel();
                    int write = sc.write(buffer);
                    System.out.println("实际写了字节数：" + write);
                    if (!buffer.hasRemaining()) {
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        key.attach(null);
                    }
                } else if (key.isReadable()) {
                    try {
                        SocketChannel sc = (SocketChannel) key.channel();
                        Map<String, ByteBuffer> map = (HashMap) key.attachment();
                        ByteBuffer buffer = map.get("read");
                        int read = sc.read(buffer);
                        if (read > 0) {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                // 切换buffer至读模式
                                buffer.flip();
                                newBuffer.put(buffer);
                                map.put("read", newBuffer);
                            }
                        } else {
                            key.cancel();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                    }
                }
            }
        }
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            char c = (char) buffer.get(i);
            if (buffer.get(i) == '\n') {
                int len = i + 1 - buffer.position();
                ByteBuffer tmp = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    tmp.put(buffer.get());
                }
                tmp.flip();
                System.out.println("客户端发送的数据 -> " + Charset.defaultCharset().decode(tmp));
            }
        }
        buffer.compact();
    }

}
