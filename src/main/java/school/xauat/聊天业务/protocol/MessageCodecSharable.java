package school.xauat.聊天业务.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import school.xauat.聊天业务.config.Config;
import school.xauat.聊天业务.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.List;

import static school.xauat.聊天业务.message.Message.getMessageClass;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 0:32
 * @description：必须和 LengthFieldBasedFrameDecoder 一起使用，确保接到的 ByteBuf 消息是完整的
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 魔数 8字节
        out.writeBytes(new byte[]{'d', 'u', 'n', 'k', 'c', 'o', 'd', 'e'});
        // 版本号 1字节
        out.writeByte(1);
        // 序列化算法 1字节 0->jdk 1->json
        // 获取对应序列化算法的值
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        //指令类型 1字节
        out.writeByte(msg.getMessageType());
        // 请求序号 4字节
        out.writeInt(msg.getSequenceId());
        // 对齐填充
        out.writeByte(0xff);
        // 获取内容的字节数组
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        // 正文长度 4 字节
        out.writeInt(bytes.length);
        // 消息正文
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] magicBytes = new byte[8];
        in.readBytes(magicBytes, 0, 8);
        String magicNum = new String(magicBytes, Charset.defaultCharset());
        byte version = in.readByte();
        // 获取序列化算法类型
        byte serializerType = in.readByte();
        // 获取信息类型
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        // 获取指定Message的类型
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = Serializer.Algorithm.values()[serializerType].deserialize(messageClass, bytes);
        // log.debug("magicNum->{};version->{};serializerType->{};messageType->{};sequenceId->{};length->{}",
        //        magicNum, version, serializerType, messageType, sequenceId, length);
        // System.out.println(message);
        out.add(message);
    }
}
