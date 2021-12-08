package school.xauat.聊天业务.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.ChatResponseMessage;
import school.xauat.聊天业务.message.GroupChatRequestMessage;
import school.xauat.聊天业务.message.GroupChatResponseMessage;
import school.xauat.聊天业务.server.session.GroupSession;
import school.xauat.聊天业务.server.session.GroupSessionFactory;

import java.util.List;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 21:12
 * @description：
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        List<Channel> channels = groupSession.getMembersChannel(groupName);
        if (channels.isEmpty()) {
            ctx.writeAndFlush(new GroupChatResponseMessage(false, groupName + "组不存在"));
        }
        for (Channel channel : channels) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
