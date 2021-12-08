package school.xauat.nio.review;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static school.xauat.util.ByteBufferUtil.debugAll;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 21:39
 * @description：
 */
public class TestByteBufferExam {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(32);
        buffer.clear();
        buffer.put(StandardCharsets.UTF_8.encode("Hello Netty\nI'm zhangsan\nHo"));
        split(buffer);
        buffer.put("w are you\n".getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        int limit = buffer.limit();
        for (int i = 0; i < limit; i++) {
            if (buffer.get(i) == '\n') {
                int len = i + 1 - buffer.position();
                ByteBuffer tmp = ByteBuffer.allocate(len);
                tmp.clear();
                for (int j = 0; j < len; j++) {
                    tmp.put(buffer.get());
                }
                tmp.flip();
                debugAll(tmp);
            }
        }

        buffer.compact();
    }
}
