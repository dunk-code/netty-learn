package school.xauat.nio.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static school.xauat.util.ByteBufferUtil.debugAll;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 12:25
 * @description：
 */
public class TestByteBufferString {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("hello Netty".getBytes());
        debugAll(buffer);

        ByteBuffer buffer2 = Charset.forName("utf-8").encode("您好");
        debugAll(buffer2);

        CharBuffer buffer3 = StandardCharsets.UTF_8.decode(buffer2);
        System.out.println(buffer3.toString());

        ByteBuffer buffer4 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer4);
    }
}
