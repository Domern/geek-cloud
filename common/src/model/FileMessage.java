package model;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public class FileMessage implements CloudMessage{

    private final String fileName;
    private final long sise;
    private final byte[] bytes;

    public FileMessage(Path file) throws IOException {
        this.fileName=file.getFileName().toString();
        this.bytes= Files.readAllBytes(file);
        this.sise=bytes.length;
    }

    public String getFileName() {
        return fileName;
    }

    public long getSise() {
        return sise;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public MessageType getType() {
        return MessageType.FILE;
    }
}
