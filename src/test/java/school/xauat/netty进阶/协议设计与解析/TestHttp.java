package school.xauat.netty进阶.协议设计与解析;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

/**
 * @author ：zsy
 * @date ：Created 2021/12/1 20:34
 * @description：
 */
@Slf4j
public class TestHttp {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            ChannelFuture channelFuture = serverBootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            // HttpRequestDecoder如站处理器 和 HttpResponseEncoder出站处理器的组合
                            ch.pipeline().addLast(new HttpServerCodec());
                            // 只接受HttpRequest过滤掉RequestContent
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
                                    log.debug(msg.uri());

                                    // 返回响应
                                    DefaultFullHttpResponse response =
                                            new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                    byte[] bytes = "<h1>Hello Netty!</h1>".getBytes();
                                    response.headers().setInt(CONTENT_LENGTH, bytes.length);
                                    response.content().writeBytes(bytes);

                                    // 写会响应
                                    ctx.writeAndFlush(response);

                                }
                            });
                            /*ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.debug("msg类型：{}", msg.getClass());
                                }
                            });*/
                        }
                    })
                    .bind(8080).sync();
            channelFuture.channel().closeFuture().addListener(future -> {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
