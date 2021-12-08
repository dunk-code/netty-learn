package school.xauat.聊天业务.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * @author ：zsy
 * @date ：Created 2021/12/5 20:23
 * @description：
 */
@Data
@ToString(callSuper = true) // 防止重写equals和hashcode方法
@AllArgsConstructor
public class RpcRequestMessage extends Message {
    /**
     * 接口名
     */
    private String interface_name;

    /**
     * 方法名
     */
    private String method_name;

    /**
     * 返回值类型
     */
    private Class<?> return_type;

    /**
     * 参数类型
     */
    private Class<?>[] parameter_types;

    /**
     * 参数值
     */
    private Object[] parameter_values;

    public RpcRequestMessage(int sequenceId, String interface_name, String method_name, Class<?> return_type, Class<?>[] parameter_types, Object[] parameter_values) {
        super.setSequenceId(sequenceId);
        this.interface_name = interface_name;
        this.method_name = method_name;
        this.return_type = return_type;
        this.parameter_types = parameter_types;
        this.parameter_values = parameter_values;
    }

    @Override
    public int getMessageType() {
        return RpcRequestMessage;
    }
}
