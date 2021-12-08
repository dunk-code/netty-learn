package school.xauat.聊天业务.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author ：zsy
 * @date ：Created 2021/12/3 20:49
 * @description：
 */
@Data
@ToString(callSuper = true)
public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}
