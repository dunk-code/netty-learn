package school.xauat.聊天业务.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.GroupChatResponseMessage;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 23:31
 * @description：
 */
@ChannelHandler.Sharable
public class GroupChatResponseMessageHandler extends SimpleChannelInboundHandler<GroupChatResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatResponseMessage msg) throws Exception {
        if (! msg.isSuccess()) {
            System.out.println(msg.getReason());
        } else
            System.out.println(msg.getFrom() + " -> " + msg.getContent());
    }
}
