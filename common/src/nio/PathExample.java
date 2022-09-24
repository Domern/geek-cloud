package nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static java.nio.file.Files.size;

public class PathExample {

    public static void main(String[] args) throws IOException {
        //1.read file bytes
        //2.write bytes to file
        //3.move in/out of directory

        Path filePath = Path.of("common", "1.txt");
        System.out.println(Files.size(filePath));

        Path dir = Path.of("common");
        System.out.println(Files.size(dir.resolve("1.txt")));

        Path dir1 = Path.of("common", "..", "common", "..", "common").normalize();
        System.out.println(dir1);
        System.out.println(dir1.toAbsolutePath());
//1
        Path file1 = dir.resolve("1.txt");
        String string = Files.readString(file1);
        System.out.println(string);
        byte[] bytes = Files.readAllBytes(file1);
        System.out.println(Arrays.toString(bytes));


        //2
        Files.copy(dir.resolve("1.JPG"),dir.resolve("copy.JPG"), StandardCopyOption.REPLACE_EXISTING);
        Files.writeString(file1,"I'm Yaroslav", StandardOpenOption.APPEND);
    }
}

