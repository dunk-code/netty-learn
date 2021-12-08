package school.xauat.nio.bytebuffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 15:32
 * @description：
 */
public class TestGatherWrites {

    public static void main(String[] args) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("word2.txt", "rw")) {
            FileChannel channel = randomAccessFile.getChannel();
            final ByteBuffer b1 = StandardCharsets.UTF_8.encode("Hello");
            final ByteBuffer b2 = StandardCharsets.UTF_8.encode("Netty");
            final ByteBuffer b3 = StandardCharsets.UTF_8.encode("您好");
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException e) {
        }
    }
}
