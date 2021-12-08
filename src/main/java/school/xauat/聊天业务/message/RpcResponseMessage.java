package school.xauat.聊天业务.message;

import lombok.Data;
import lombok.ToString;

/**
 * @author ：zsy
 * @date ：Created 2021/12/5 20:24
 * @description：
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {

    private Object return_value;

    private Exception exception_value;

    @Override
    public int getMessageType() {
        return RpcResponseMessage;
    }
}
