package school.xauat.nio.bytebuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static school.xauat.util.ByteBufferUtil.debugAll;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 15:21
 * @description：
 */
public class TestScatteringReads {

    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("word1.txt").getChannel()) {
            ByteBuffer a = ByteBuffer.allocate(3);
            ByteBuffer b = ByteBuffer.allocate(3);
            ByteBuffer c = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{a, b, c});
            a.flip();
            b.flip();
            c.flip();
            debugAll(a);
            debugAll(b);
            debugAll(c);
        } catch (IOException e) {
        }
    }
}
