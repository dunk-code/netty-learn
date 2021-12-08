package school.xauat.聊天业务.config;

import school.xauat.聊天业务.protocol.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author ：zsy
 * @date ：Created 2021/12/4 14:58
 * @description：
 */
public class Config {

    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")) {
             properties = new Properties();
             properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static int getServerPort() {
        String value = properties.getProperty("server.port");
        if (value == null) {
            return 8080;
        } else {
            return Integer.parseInt(value);
        }
    }

    public static Serializer.Algorithm getSerializerAlgorithm() {
        String value = properties.getProperty("serializer.algorithm");
        if (value == null) {
            return Serializer.Algorithm.java;
        } else {
            return Serializer.Algorithm.valueOf(value);
        }
    }
}
