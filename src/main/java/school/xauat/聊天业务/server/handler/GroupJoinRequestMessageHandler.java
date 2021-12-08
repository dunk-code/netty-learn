package school.xauat.聊天业务.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.GroupJoinRequestMessage;
import school.xauat.聊天业务.message.GroupJoinResponseMessage;
import school.xauat.聊天业务.server.session.Group;
import school.xauat.聊天业务.server.session.GroupSessionFactory;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 21:41
 * @description：
 */
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession().joinMember(msg.getGroupName(), msg.getUsername());
        if (group != null) {
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, msg.getGroupName() + "群加入成功"));
        } else {
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, msg.getGroupName() + "群不存在"));
        }
    }
}
