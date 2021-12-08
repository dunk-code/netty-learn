package school.xauat.netty.组件.channel.future;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author ：zsy
 * @date ：Created 2021/11/26 20:14
 * @description：
 */
@Slf4j
public class TestJDKFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建线程池
        ThreadPoolExecutor thread_pool = new ThreadPoolExecutor(
                2,
                2,
                0L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(2),
                new ThreadPoolExecutor.AbortPolicy());
        Future<Integer> submit = thread_pool.submit(() -> {
            log.debug("正在计算结果...");
            TimeUnit.SECONDS.sleep(1);
            return 50;
        });
        log.debug("计算结果 -> {}", submit.get());
    }

}
