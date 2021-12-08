package school.xauat.nio.网络编程.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author ：zsy
 * @date ：Created 2021/11/21 16:15
 * @description：
 */
public class Client {

    public static void main(String[] args) {
        try (SocketChannel sc = SocketChannel.open()) {
            sc.connect(new InetSocketAddress("localhost", 8080));
            sc.write(Charset.defaultCharset().encode("Hello\nWorld-Netty-Zhang-San\n"));
            while (true) {
                System.out.println("请输入你想要发送的文字");
                Scanner in = new Scanner(System.in);
                String next = in.nextLine();
                System.out.println(next);
                sc.write(Charset.defaultCharset().encode(next + ""));
            }
            // System.in.read();
        } catch (IOException e) {
        }
    }
}
