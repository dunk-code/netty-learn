package school.xauat.nio.review;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：zsy
 * @date ：Created 2021/11/23 14:19
 * @description：
 */
@Slf4j
public class MultithreadingServer {

    public static void main(String[] args) {
        new Boss("boss").register();
    }

    static class Boss implements Runnable {
        private String name;
        private Selector selector;
        private Worker[] workers;
        private volatile boolean start = false;
        private AtomicInteger index = new AtomicInteger(0);

        public Boss(String name) {
            this.name = name;
        }

        public void register() {
            if (!start) {
                try {
                    ServerSocketChannel ssc = ServerSocketChannel.open();
                    ssc.configureBlocking(false);
                    ssc.bind(new InetSocketAddress(8080));

                    this.selector = Selector.open();
                    ssc.register(selector, SelectionKey.OP_ACCEPT, null);
                    workers = initWorkers();
                    new Thread(this, name).start();
                    log.debug("boss start -> {}", ssc);
                    start = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        private Worker[] initWorkers() {
            int len = Runtime.getRuntime().availableProcessors();
            Worker[] workers = new Worker[len];
            for (int i = 0; i < len; i++) {
                workers[i] = new Worker("worker_" + i);
            }
            return workers;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                            SocketChannel sc = ssc.accept();
                            sc.configureBlocking(false);
                            log.debug("connected -> {}", sc);
                            workers[index.getAndIncrement() % workers.length].register(sc);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Worker implements Runnable {
        private String name;
        private Selector selector;
        private volatile boolean start;

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel sc) {
            if (!start) {
                try {
                    this.selector = Selector.open();
                    new Thread(this, name).start();
                    start = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            selector.wakeup();
            try {
                sc.register(selector, SelectionKey.OP_READ, null);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isReadable()) {
                            try {
                                SocketChannel sc = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(32);
                                log.debug("before read -> {}", sc);
                                int read = sc.read(buffer);
                                if (read > 0) {
                                    buffer.flip();
                                    System.out.println(Charset.defaultCharset().decode(buffer));
                                    buffer.clear();
                                    log.debug("after read -> {}", sc);
                                } else {
                                    key.cancel();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                key.cancel();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
