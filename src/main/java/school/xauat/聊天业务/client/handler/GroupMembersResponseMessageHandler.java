package school.xauat.聊天业务.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.GroupMembersResponseMessage;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 23:35
 * @description：
 */
@ChannelHandler.Sharable
public class GroupMembersResponseMessageHandler extends SimpleChannelInboundHandler<GroupMembersResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersResponseMessage msg) throws Exception {
        System.out.println(msg.getMembers());
    }
}
