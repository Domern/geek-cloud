package nio.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.file.Path.of;

public class TelnetTerminal {

    /**
     * Support commands:
     * cd path - go to dir
     * touch filename - create file with filename
     * mkdir dirname - create directory with dirname
     * cat filename - show filename bytes
     */

    private Path current;
    private ServerSocketChannel server;
    private Selector selector;

    private ByteBuffer buf;
    private SocketChannel channel;

    public TelnetTerminal() throws IOException {
        current = of("common");
        buf = ByteBuffer.allocate(256);
        server = ServerSocketChannel.open();
        selector = Selector.open();
        server.bind(new InetSocketAddress(8189));
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        while (server.isOpen()) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = keys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {
                    handleAccept();
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                keyIterator.remove();
            }
        }
    }

    private void handleRead(SelectionKey key) throws IOException {
        channel = (SocketChannel) key.channel();
        buf.clear();
        StringBuilder sb = new StringBuilder();
        while (true) {
            int read = channel.read(buf);
            if (read == 0) {
                break;
            }
            if (read == -1) {
                channel.close();
                return;
            }
            buf.flip();
            while (buf.hasRemaining()) {
                sb.append((char) buf.get());
            }
            buf.clear();
        }
        System.out.println("Received: " + sb);
        String command = sb.toString().trim();
        if (command.equals("ls")) {
            lsCommand();
        } else if (command.startsWith("cd")) {
            cdCommand(command);
        } else if (command.startsWith("touch")) {
            touchCommand(command);
        } else if (command.startsWith("mkdir")) {
            mkdirCommand(command);
        } else if (command.startsWith("cat")) {
            catCommand(command);
        } else {
            byte[] bytes = command.getBytes(StandardCharsets.UTF_8);
            channel.write(ByteBuffer.wrap(bytes));
        }
    }

    private void catCommand(String command) {
        String filename=command.substring(4);
        Path bytefile=Path.of(String.valueOf(current), filename).normalize();
        try {
            byte[] bytes = Files.readAllBytes(bytefile);
            String string=Arrays.toString(bytes);
            System.out.println(Arrays.toString(bytes));
            channel.write(ByteBuffer.wrap(string.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void mkdirCommand(String command) {
        String dirname=command.substring(6);
        Path newdir=Path.of(String.valueOf(current), dirname).normalize();
        try {
            if(!Files.exists(newdir)) {
                Files.createDirectory(newdir);
            }else {
                channel.write(ByteBuffer.wrap("This directory is already available".getBytes()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void touchCommand(String command) {
        String filename=command.substring(6);
        Path newfile=Path.of(String.valueOf(current), filename).normalize();
        try {
            if(!Files.exists(newfile)) {
                Files.createFile(newfile);
            }else {
                channel.write(ByteBuffer.wrap("This file is already available".getBytes()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void lsCommand() throws IOException {
        String files = Files.list(current)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.joining("\n\r"));
        channel.write(ByteBuffer.wrap(files.getBytes(StandardCharsets.UTF_8)));
    }

    private void cdCommand(String command) throws IOException {
        String dirname = null;
        if (command.length() > 3) {
            dirname = command.substring(3);
            if (Files.isDirectory(of(dirname))) {
                if (dirname.equals("common")) {
                    current = Path.of("common").normalize();
                } else {
                    current = Path.of("common", dirname).normalize();
                }
            } else
                channel.write(ByteBuffer.wrap("it's not directory".getBytes()));
        } else {
            channel.write(ByteBuffer.wrap("dirname?".getBytes()));
        }

    }


    private void handleAccept() throws IOException {
        SocketChannel socketChannel = server.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Client accepted");
    }

    public static void main(String[] args) throws IOException {
        new TelnetTerminal();
    }
}
