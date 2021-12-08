package school.xauat.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import school.xauat.聊天业务.protocol.Serializer;

/**
 * @author ：zsy
 * @date ：Created 2021/12/6 16:11
 * @description：
 */
public class TestClassCodec {

    @Test
    public void test() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
        String json = gson.toJson(String.class);
        System.out.println(json);
        String s = gson.fromJson(json, String.class);
        System.out.println(s);

    }
}
