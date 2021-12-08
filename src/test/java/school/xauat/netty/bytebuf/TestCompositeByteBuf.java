package school.xauat.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static school.xauat.util.ByteBufUtil.log;

/**
 * @author ：zsy
 * @date ：Created 2021/11/30 21:59
 * @description：
 */
public class TestCompositeByteBuf {

    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});
        CompositeByteBuf bufs = ByteBufAllocator.DEFAULT.compositeBuffer();
        bufs.addComponents(true, buf1, buf2);
        log(bufs);
    }

}
