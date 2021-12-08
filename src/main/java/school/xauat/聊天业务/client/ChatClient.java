package school.xauat.聊天业务.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import school.xauat.聊天业务.client.handler.*;
import school.xauat.聊天业务.message.*;
import school.xauat.聊天业务.protocol.MessageCodecSharable;
import school.xauat.聊天业务.protocol.ProcotolFrameDecoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        // 使用CountDownLatch来进行线程之间通信
        CountDownLatch WAIT_FOR_RESPONSE = new CountDownLatch(1);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        // 使用AtomicBoolean来记录登录是否成功
        AtomicBoolean LOGIN_SUCCESS = new AtomicBoolean();
        AtomicBoolean EXIT = new AtomicBoolean();
        // ConcurrentLinkedQueue<Message> MESSAGE_QUEUE = new ConcurrentLinkedQueue<>();
        ChatResponseMessageHandler CHAT_HANDLER = new ChatResponseMessageHandler();
        GroupCreateResponseMessageHandler GROUP_CREATE_HANDLER = new GroupCreateResponseMessageHandler();
        GroupChatResponseMessageHandler GROUP_CHAR_HANDLER = new GroupChatResponseMessageHandler();
        GroupMembersResponseMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersResponseMessageHandler();
        GroupJoinResponseMessageHandler GROUP_JOIN_HANDLER = new GroupJoinResponseMessageHandler();
        GroupQuitResponseMessageHandler GROUP_QUIT_HANDLER = new GroupQuitResponseMessageHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    // ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    // 添加一个空闲时间处理器，作为心跳包如果客户端超过三秒没有向服务器发送数据
                    // 那么触发一个IdleState#WRITE_IDLE
                    ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                    // 创建一个ChannelDuplexHandler来处理IdleState#WRITE_IDLE事件
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (event.state() == IdleState.WRITER_IDLE) {
                                // log.debug("3s没有发送数据了，发送一个心跳包");
                                ctx.channel().writeAndFlush(new PingMessage());
                            }
                        }
                    });
                    // 客户端建立连接后，进行登录验证
                    ch.pipeline().addLast(CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAR_HANDLER);
                    ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                    ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                    ch.pipeline().addLast("client_handler", new ChannelInboundHandlerAdapter() {

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            // log.debug("msg->{}", msg);
                            WAIT_FOR_RESPONSE.countDown();
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                // MESSAGE_QUEUE.add(response);
                                if (response.isSuccess()) {
                                    LOGIN_SUCCESS.compareAndSet(false, true);
                                }
                            }
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 防止阻塞NIO线程，开启一个新的线程
                            new Thread(() -> {
                                Scanner in = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = in.nextLine();
                                if (EXIT.get()) return;
                                System.out.println("请输入登录密码：");
                                String password = in.nextLine();
                                if (EXIT.get()) return;
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(message);

                                System.out.println("执行后序操作...");
                                try {
                                    WAIT_FOR_RESPONSE.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // 登录失败

                                if (!LOGIN_SUCCESS.get()) {
                                    ctx.channel().close();
                                    return;
                                }
                                /*if (!MESSAGE_QUEUE.isEmpty()) {
                                    LoginResponseMessage response = (LoginResponseMessage) MESSAGE_QUEUE.poll();
                                    if (!response.isSuccess()) {
                                        System.out.println(response.getReason());
                                        ctx.channel().close();
                                        return;
                                    }
                                }*/

                                // 登录成功
                                while (true) {
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = in.nextLine();
                                    String[] s = command.split(" ");
                                    if (EXIT.get()) return;
                                    switch (s[0]) {
                                        case "send" :
                                            ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gsend" :
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gcreate" :
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            set.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                            break;
                                        case "gmembers" :
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin" :
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                            break;
                                        case "gquit" :
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                            break;
                                        case "quit" :
                                            ctx.channel().close();
                                            return;
                                    }
                                }

                            }, "login_thread").start();
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("连接已断开，按任意键退出...");
                            EXIT.compareAndSet( false, true);
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            log.debug("连接已断开，按任意键退出...{}", cause.getMessage());
                            EXIT.compareAndSet(false, true);
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
