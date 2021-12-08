package school.xauat.聊天业务.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.GroupJoinResponseMessage;
import school.xauat.聊天业务.message.GroupQuitResponseMessage;
import school.xauat.聊天业务.server.session.Group;
import school.xauat.聊天业务.server.session.GroupSessionFactory;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 21:43
 * @description：
 */
@ChannelHandler.Sharable
public class GroupQuitResponseMessageHandler extends SimpleChannelInboundHandler<GroupQuitResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitResponseMessage msg) throws Exception {
        System.out.println(msg.getReason());
    }
}
