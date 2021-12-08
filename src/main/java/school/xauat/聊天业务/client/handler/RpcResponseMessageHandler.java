package school.xauat.聊天业务.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import school.xauat.聊天业务.message.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：zsy
 * @date ：Created 2021/12/5 21:22
 * @description：
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {

    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("{}", msg);
        // 对象使用完毕，移除对象
        Promise<Object> promise = PROMISES.remove(msg.getSequenceId());
        if (promise != null) {
            Object return_value = msg.getReturn_value();
            Exception exception_value = msg.getException_value();
            if (exception_value == null) {
                promise.setSuccess(return_value);
            } else {
                promise.setFailure(exception_value);
            }
        }
    }
}

