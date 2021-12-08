package school.xauat.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author ：zsy
 * @date ：Created 2021/11/19 16:21
 * @description：
 */
public class HelloNettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *
     * @param ctx
     * @param msg
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) {
                System.out.println((char) in.readableBytes());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     *
     * @param ctx
     * @param cause
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
