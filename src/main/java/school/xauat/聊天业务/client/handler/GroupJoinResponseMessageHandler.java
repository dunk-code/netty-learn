package school.xauat.聊天业务.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.GroupJoinResponseMessage;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 23:36
 * @description：
 */
@ChannelHandler.Sharable
public class GroupJoinResponseMessageHandler extends SimpleChannelInboundHandler<GroupJoinResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinResponseMessage msg) throws Exception {
        System.out.println(msg.getReason());
    }
}
