package school.xauat.netty.组件.channel.future;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author ：zsy
 * @date ：Created 2021/11/26 20:24
 * @description：
 */

@Slf4j
public class TestNettyFuture {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 创建时间循环组
        NioEventLoopGroup executors = new NioEventLoopGroup();

        Future<Integer> future = executors.submit(() -> {
            log.debug("正在计算结果....");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 30;
        });

        log.debug("等待计算结果...");
        /*Integer now = future.getNow();
        while (now == null) {
            Thread.sleep(200);
            log.debug("获取到的结果 -> {}", now);
            now = future.getNow();
        }
        log.debug("获取到的结果 -> {}", now);*/

        // 同步阻塞
        /*future.sync();
        System.out.println(future.get());
        log.debug("获取到计算结果...");*/

        // 异步等待
        future.addListener(new GenericFutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                log.debug("获取到的结果 -> {}", future.get());
            }
        });
    }

}
