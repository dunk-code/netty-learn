package school.xauat.nio.bytebuffer;


import java.nio.ByteBuffer;

import static school.xauat.util.ByteBufferUtil.debugAll;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 15:54
 * @description：
 */
public class TestByteBufferExam {

    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.clear();
        source.put("Hello World\nI'm zhangsan\nHo".getBytes());
        split(source);
        source.put("w are you\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if (source.get(i) == '\n') {
                int len = i + 1 - source.position();
                ByteBuffer tmp = ByteBuffer.allocate(len);
                tmp.clear();
                for (int j = 0; j < len; j++) {
                    tmp.put(source.get());
                }
                tmp.flip();
                debugAll(tmp);
            }
        }
        //可能没有读完，不能使用clear
        source.compact();
    }
}
