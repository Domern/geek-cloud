package model;

import lombok.Getter;

@Getter
public class NewFileName implements CloudMessage{
    private String lastFileName;
    private String newFuleName;

    public NewFileName(String lastFileName, String newFuleName) {
        this.lastFileName = lastFileName;
        this.newFuleName = newFuleName;
    }

    @Override
    public MessageType getType() {
        return MessageType.NEWFILENAME;
    }
}
