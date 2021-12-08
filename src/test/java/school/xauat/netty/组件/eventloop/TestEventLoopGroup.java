package school.xauat.netty.组件.eventloop;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author ：zsy
 * @date ：Created 2021/11/24 15:46
 * @description：
 */
@Slf4j
public class TestEventLoopGroup {

    public static void main(String[] args) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup(2);
        // System.out.println(NettyRuntime.availableProcessors());

        // 获取下一个事件循环对象
        /*System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());
        System.out.println(eventLoopGroup.next());*/

        // 执行普通任务
        /*eventLoopGroup.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("普通任务...");
        });*/

        // 执行定时任务
        eventLoopGroup.scheduleAtFixedRate(() -> {
            log.debug("定时任务...");
        }, 0, 1, TimeUnit.SECONDS);

        log.debug("main...");
    }
}
