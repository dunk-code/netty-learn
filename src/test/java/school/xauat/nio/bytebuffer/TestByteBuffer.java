package school.xauat.nio.bytebuffer;


import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author ：zsy
 * @date ：Created 2021/11/19 20:33
 * @description：测试ByteBuffer
 */
@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        // FileChannel
        // 通过输入输出流获取文件 或者 RandomAccessFile
        // try-with-resource语法无需自己写代码关闭资源，资源必须实现AutoClosable接口，重写close方法
        //原理：编译器自动帮我们生成了finally块，并且在里面调用了资源的close方法
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从channel中读数据，向buffer写入数据
                int len = channel.read(buffer);
                log.debug("读到字节数：{}", len);
                if (len == -1) break;
                // 切换至读模式
                buffer.flip();
                // 打印buffer中的内容
                while (buffer.hasRemaining()) { // 还有剩余未读数据
                    byte b = buffer.get();
                    System.out.print((char) b);
                }
                // 切换至写模式
                buffer.clear();
            }
        } catch (IOException e) {
        }
        ;
    }
}
