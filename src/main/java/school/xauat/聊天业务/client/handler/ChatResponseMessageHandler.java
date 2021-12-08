package school.xauat.聊天业务.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.ChatResponseMessage;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 20:50
 * @description：
 */
@ChannelHandler.Sharable
public class ChatResponseMessageHandler extends SimpleChannelInboundHandler<ChatResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatResponseMessage msg) throws Exception {
        System.out.println(msg.getFrom() + " -> " + msg.getContent());
    }
}
