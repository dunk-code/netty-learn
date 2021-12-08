package school.xauat.nio.bytebuffer;

import java.nio.ByteBuffer;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 11:48
 * @description：
 */
public class TestByteBufferAllocate {
    public static void main(String[] args) {
        System.out.println(ByteBuffer.allocate(16));            // java.nio.HeapByteBuffer[pos=0 lim=16 cap=16]
        System.out.println(ByteBuffer.allocateDirect(16));      // java.nio.DirectByteBuffer[pos=0 lim=16 cap=16]
    }
}
