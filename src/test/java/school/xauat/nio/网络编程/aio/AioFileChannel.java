package school.xauat.nio.网络编程.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static school.xauat.util.ByteBufferUtil.debugRead;

/**
 * @author ：zsy
 * @date ：Created 2021/11/23 19:55
 * @description：
 */
@Slf4j
public class AioFileChannel {

    public static void main(String[] args) {
        try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(Paths.get("data.txt"), StandardOpenOption.READ)) {

            ByteBuffer buffer = ByteBuffer.allocate(16);
            log.debug("read begin...");
            // 参数1：ByteBuffer
            // 参数2：读取的起始位置
            // 参数3：附加
            // 参数4：回调函数，以守护线程的形式回调
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    log.debug("read completed...{}", result);
                    attachment.flip();
                    // System.out.println(Charset.defaultCharset().decode(attachment));
                    debugRead(buffer);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    exc.printStackTrace();
                }
            });
            log.debug("read end");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
