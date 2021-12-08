package school.xauat.nio.文件编程;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author ：zsy
 * @date ：Created 2021/11/21 10:24
 * @description：
 */
public class TestFileCopy {

    public static void main(String[] args) throws IOException {
        String source = "E:\\course\\31-Netty\\Netty学习资料\\视频";
        // String target = "E:\\course\\31-Netty\\Netty学习资料\\视频";
        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                // String targetName = path.toString().replace(source, target);
                if (Files.isDirectory(path)) {
                    // Files.createDirectory(Paths.get(targetName));
                } else if (Files.isRegularFile(path)) {
                    File file = path.toFile();
                    String string = file.getName();
                    int index = string.indexOf("_P");
                    if (index != -1 && index != 0) {
                        string = string.substring(index + 1);
                        Files.copy(path, Paths.get(source + "\\" + string));
                        Files.delete(path);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
