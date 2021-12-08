package school.xauat.nio.文件编程;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：zsy
 * @date ：Created 2021/11/20 22:27
 * @description：
 */
public class TestFilesWalkFileTree {
    public static void main(String[] args) throws IOException {
        AtomicInteger dirCount = new AtomicInteger();
        AtomicInteger fileCount = new AtomicInteger();
        AtomicInteger jarCount = new AtomicInteger();
        Files.walkFileTree(Paths.get("E:\\java tools\\JDK8\\jdk1.8.0_101"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>" + dir);
                dirCount.getAndIncrement();
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                fileCount.getAndIncrement();
                if (file.toFile().getName().endsWith(".jar")) {
                    jarCount.getAndIncrement();
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("目录数量：" + dirCount);
        System.out.println("文件数量：" + fileCount);
        System.out.println("jar文件数量：" + jarCount);
    }

    @Test
    public void remove_dir() throws IOException {
        Files.walkFileTree(Paths.get("C:\\Users\\dell\\Desktop\\target"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file.toFile().toString());
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("====>" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("删除" + dir);
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }
}

