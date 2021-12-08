package school.xauat.聊天业务.protocol;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：zsy
 * @date ：Created 2021/12/6 17:23
 * @description：
 */
public class SequenceIdGenerator {
    private static final AtomicInteger sequenceId = new AtomicInteger(new Random().nextInt(100000));

    public static int getSequenceId() {
        return sequenceId.getAndIncrement();
    }
}
