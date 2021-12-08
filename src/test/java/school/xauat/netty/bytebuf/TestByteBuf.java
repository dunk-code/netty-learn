package school.xauat.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;
import java.util.Arrays;

import static school.xauat.util.ByteBufUtil.log;

/**
 * @author ：zsy
 * @date ：Created 2021/11/29 19:55
 * @description：
 */
public class TestByteBuf {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(12);
        // 创建基于堆的
        // ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
        // ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer();
        System.out.println(buf.getClass());
        log(buf);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            builder.append("a");
        }
        // buf.writeBytes(builder.toString().getBytes());
        buf.writeInt(5);
        buf.writeBytes(new byte[]{1, 2, 3, 4});
        buf.writeInt(6);
        log(buf);
        buf.markReaderIndex();
        System.out.println(buf.readInt());
        byte[] bytes = new byte[4];
        // buf.readBytes(bytes);
        // System.out.println(Arrays.toString(bytes));
        // StringBuilder builder1 = (StringBuilder) buf.readCharSequence(4, Charset.defaultCharset());

        buf.resetReaderIndex();
        System.out.println(buf.readInt());
        log(buf);
    }

}
