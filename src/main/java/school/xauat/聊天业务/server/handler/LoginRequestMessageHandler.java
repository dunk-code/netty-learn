package school.xauat.聊天业务.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import school.xauat.聊天业务.message.LoginRequestMessage;
import school.xauat.聊天业务.message.LoginResponseMessage;
import school.xauat.聊天业务.server.service.UserServiceFactory;
import school.xauat.聊天业务.server.session.SessionFactory;

/**
 * @author ：zsy
 * @date ：Created 2021/12/2 20:29
 * @description：
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        boolean ok = UserServiceFactory.getUserService().login(msg.getUsername(), msg.getPassword());
        LoginResponseMessage message;
        if (ok) {
            SessionFactory.getSession().bind(ctx.channel(), msg.getUsername());
            message = new LoginResponseMessage(true, "登录成功");
        } else {
            message = new LoginResponseMessage(false, "用户名或密码错误");
        }
        ctx.writeAndFlush(message);
    }
}
