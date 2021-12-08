package school.xauat.聊天业务.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.ChatRequestMessage;
import school.xauat.聊天业务.message.ChatResponseMessage;
import school.xauat.聊天业务.server.session.SessionFactory;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 20:32
 * @description：
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        // 获取对应用户的channel
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        } else {
            ctx.writeAndFlush(new ChatResponseMessage(false,  "用户" + to + "不在线或者不存在"));
        }

    }
}
