package com.geekbrains.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiveFileList extends AbstractMessage{
    private final String path;

    public GiveFileList(String string) {
        this.path = string;
    }
    @Override
    public CommandType getType() {
        return CommandType.GIVELIST;
    }
}
