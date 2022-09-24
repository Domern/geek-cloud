package nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.RandomAccess;

public class ChannelExample {
    public static void main(String[] args) {
        // ByteBuffer read
        // ByteBuffer write

        ByteBuffer buf = ByteBuffer.allocate(15);
        buf.put("Hello world!".getBytes());
        buf.flip();//set limit 0   set position 0
        StringBuilder sb = new StringBuilder();
        while (buf.hasRemaining()) {
            byte b = buf.get();
            sb.append((char) b);
        }
       System.out.println(sb);

//        buf.rewind(); //read without mutate  set position 0
//        buf.mark(); //marking cell
//        buf.reset(); //reset to mark cell


        //Chanel read/write
        buf.clear();
        sb = new StringBuilder();
        try (RandomAccessFile raf = new RandomAccessFile("common/1.txt", "rw")) {
            FileChannel channel = raf.getChannel();
            channel.write(ByteBuffer.wrap("New message".getBytes()), channel.size());
           // channel.position(14);
            while (true) {
                int read = channel.read(buf);
                if (read == -1) {
                    break;
                }
                buf.flip();
                while (buf.hasRemaining()) {
                    sb.append((char) buf.get());
                };
                buf.clear();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sb);

    }
}
