package school.xauat.nio.网络编程.多线程;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author ：zsy
 * @date ：Created 2021/11/22 17:03
 * @description：
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));

        sc.write(Charset.defaultCharset().encode("Hello Netty"));

        System.in.read();
    }

}
