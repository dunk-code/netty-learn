package school.xauat.netty.组件.channel.future;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author ：zsy
 * @date ：Created 2021/11/26 23:54
 * @description：
 */
@Slf4j
public class TestNettyPromise {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        DefaultEventLoop executors = new DefaultEventLoop();
        Promise<Integer> promise = new DefaultPromise<>(executors);

        new Thread(() -> {
            log.debug("开始计算...");
            try {
                int i = 10 / 0;
                TimeUnit.SECONDS.sleep(1);
                promise.setSuccess(10);
            } catch (InterruptedException e) {
                // e.printStackTrace();
                promise.setFailure(e);
            }
        }, "son").start();

        log.debug("等待处理结果...");
        log.debug("处理结果 -> {}", promise.get());
    }

}
