package school.xauat.nio.文件编程;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 16:33
 * @description：
 */
public class TestFileChannelTransferTo {

    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new RandomAccessFile("to.txt", "rw").getChannel()
        ) {
            long size = from.size();
            for (long left = size; left > 0;) {
                System.out.println("position：" + (size - left) + "   count：" + left);
                left -= from.transferTo(left - size, left, to);
            }
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }

}
