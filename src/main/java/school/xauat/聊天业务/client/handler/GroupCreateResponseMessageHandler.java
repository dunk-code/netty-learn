package school.xauat.聊天业务.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.GroupCreateResponseMessage;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 23:28
 * @description：
 */
@ChannelHandler.Sharable
public class GroupCreateResponseMessageHandler extends SimpleChannelInboundHandler<GroupCreateResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateResponseMessage msg) throws Exception {
        System.out.println(msg.getReason());
    }
}
