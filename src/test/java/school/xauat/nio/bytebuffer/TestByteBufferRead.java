package school.xauat.nio.bytebuffer;

import java.nio.ByteBuffer;

import static school.xauat.util.ByteBufferUtil.debugAll;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 12:07
 * @description：
 */
public class TestByteBufferRead {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();

        /*buffer.get(new byte[4]);
        buffer.rewind();
        debugAll(buffer);*/

        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        debugAll(buffer);
        buffer.reset();
        debugAll(buffer);

    }
}
