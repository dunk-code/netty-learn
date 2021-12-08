package school.xauat.nio.bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author ：zsy
 * @date ：Created 2021/11/19 20:51
 * @description：
 */
@Slf4j
public class TestByteBuffer0 {

    public static void main(String[] args) {

        try (RandomAccessFile file = new RandomAccessFile("data.txt", "rw")) {
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                int len = channel.read(buffer);
                buffer.flip();
                log.debug("读取到的字节数：{}", len);
                if (len == -1) break;
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    System.out.println((char) b);
                }
                buffer.clear();
            }
        } catch (IOException e) {
        }
    }
}
