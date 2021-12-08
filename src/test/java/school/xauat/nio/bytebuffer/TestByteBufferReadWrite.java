package school.xauat.nio.bytebuffer;

import java.nio.ByteBuffer;

import static school.xauat.util.ByteBufferUtil.debugAll;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 0:09
 * @description：
 */
public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(7);
        buffer.put((byte) 'a');
        buffer.put((byte) 'b');
        buffer.flip();
        byte b = buffer.get();
        System.out.println((char) b);
        buffer.compact();
        debugAll(buffer);
    }
}
