package model;

import lombok.Getter;

@Getter
public class DeleteFile implements CloudMessage{
    private String deleteFileName;

    public DeleteFile(String deleteFileName) {
        this.deleteFileName = deleteFileName;
    }

    @Override
    public MessageType getType() {
        return null;
    }
}
