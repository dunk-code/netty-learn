package school.xauat.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static school.xauat.util.ByteBufUtil.log;

/**
 * @author ：zsy
 * @date ：Created 2021/11/30 21:36
 * @description：
 */
public class TestSlice {

    public static void main(String[] args) {
        byte[] bytes = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        buf.writeBytes(bytes);
        log(buf);
        buf.retain();
        ByteBuf f1 = buf.slice(0, 5);
        ByteBuf f2 = buf.slice(5, 5);
        log(f1);
        log(f2);

    }

}
