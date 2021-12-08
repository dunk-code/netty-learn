package school.xauat.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import school.xauat.聊天业务.message.LoginRequestMessage;
import school.xauat.聊天业务.protocol.MessageCodec;

import java.util.concurrent.TimeUnit;

/**
 * @author ：zsy
 * @date ：Created 2021/12/1 22:03
 * @description：
 */
public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
                new LengthFieldBasedFrameDecoder(1024, 16, 4, 0, 0),
                new MessageCodec()
        );

        // encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123");
        // channel.writeOutbound(message);

        // decode
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buffer);
        // 测试半包问题
        ByteBuf buf1 = buffer.slice(0, 100);
        buf1.retain();
        ByteBuf buf2 = buffer.slice(100, buffer.readableBytes() - 100);
        buf2.retain();
        channel.writeInbound(buf1); // writeInbound方法执行完后会调用buf的release方法

        channel.writeInbound(buf2);

        channel.writeInbound(buffer);
    }
}
