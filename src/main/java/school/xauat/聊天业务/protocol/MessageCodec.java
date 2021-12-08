package school.xauat.聊天业务.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import school.xauat.聊天业务.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author ：zsy
 * @date ：Created 2021/12/1 21:33
 * @description：
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 魔数 8字节
        out.writeBytes(new byte[]{'d', 'u', 'n', 'k', 'c', 'o', 'd', 'e'});
        // 版本号 1字节
        out.writeByte(1);
        // 序列化算法 1字节 0->jdk 2->json
        out.writeByte(0);
        //指令类型 1字节
        out.writeByte(msg.getMessageType());
        // 请求序号 4字节
        out.writeInt(msg.getSequenceId());
        // 对齐填充
        out.writeByte(0xff);
        // 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        // 正文长度 4 字节
        out.writeInt(bytes.length);
        // 消息正文
        out.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] magicBytes = new byte[8];
        in.readBytes(magicBytes, 0, 8);
        String magicNum = new String(magicBytes, Charset.defaultCharset());
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();
        log.debug("magicNum->{};version->{};serializerType->{};messageType->{};sequenceId->{};length->{}",
                magicNum, version, serializerType, messageType, sequenceId, length);
        System.out.println(message);
        out.add(message);
    }

}
