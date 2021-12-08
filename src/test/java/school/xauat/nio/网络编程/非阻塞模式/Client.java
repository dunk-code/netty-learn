package school.xauat.nio.网络编程.非阻塞模式;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author ：zsy
 * @date ：Created 2021/11/21 11:18
 * @description：
 */
public class Client {

    public static void main(String[] args) throws IOException {
        // 创建客户端
        SocketChannel sc = SocketChannel.open();

        // 连接服务端
        sc.connect(new InetSocketAddress("localhost", 8080));

        System.out.println("wait......");
    }

}
