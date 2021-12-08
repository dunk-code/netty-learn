package school.xauat.聊天业务.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.GroupMembersRequestMessage;
import school.xauat.聊天业务.message.GroupMembersResponseMessage;
import school.xauat.聊天业务.server.session.GroupSessionFactory;

import java.util.Set;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 21:32
 * @description：
 */
@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        Set<String> members = GroupSessionFactory.getGroupSession().getMembers(msg.getGroupName());
        ctx.writeAndFlush(new GroupMembersResponseMessage(members));
    }
}
