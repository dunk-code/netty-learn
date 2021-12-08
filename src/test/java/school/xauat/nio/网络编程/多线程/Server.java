package school.xauat.nio.网络编程.多线程;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static school.xauat.util.ByteBufferUtil.debugRead;

/**
 * @author ：zsy
 * @date ：Created 2021/11/22 16:44
 * @description： boss线程主要负责连接。accept事件，worker线程负责read事件的监听
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));

        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT, null);

        Worker[] workers = new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker_" + i);
        }

        AtomicInteger index = new AtomicInteger(0);
        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    log.debug("connect -> {}", sc);
                    sc.configureBlocking(false);
                    // 轮询获取worker
                    workers[index.getAndIncrement() % workers.length].register(sc);
                }
            }
        }
    }

    static class Worker implements Runnable {
        private String name;
        private Selector selector;
        private Thread thread;
        private boolean start;
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                this.selector = Selector.open();
                this.thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            // 在注册事件时，为了防止worker_0线程被阻塞，需要使用wakeup()唤醒线程
            // wakeup():相当于一个通行证，可以使用一次，无论先后顺序
            selector.wakeup();
            sc.register(selector, SelectionKey.OP_READ, null);

            // 使用队列进行线程间通信
            /*queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ, null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();*/
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Runnable task = queue.poll();
                    if (task != null) {
                        task.run();
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            log.debug("before read -> {}", channel);
                            int read = channel.read(buffer);
                            if (read > 0) {
                                buffer.flip();
                                debugRead(buffer);
                                System.out.println(Charset.defaultCharset().decode(buffer));
                                buffer.clear();
                            }
                            log.debug("after read -> {}", channel);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
