package model;

import lombok.Getter;

@Getter
public class FileRequest implements CloudMessage{
private final String fileName;

    public FileRequest(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public MessageType getType() {
        return MessageType.FILE_REQUEST;
    }
}
