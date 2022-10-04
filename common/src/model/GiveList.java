package model;

import lombok.Getter;

@Getter

public class GiveList implements CloudMessage{
    private final String path;

    public GiveList(String string) {
        this.path = string;
    }

    @Override
    public MessageType getType() {
        return MessageType.GIVELIST;
    }
}
