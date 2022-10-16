package com.geekbrains.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewFileName extends AbstractMessage {
    private String lastFileName;
    private String newFileName;

    public NewFileName(String lastFileName, String newFileName) {
        this.lastFileName = lastFileName;
        this.newFileName = newFileName;
    }

    @Override
    public CommandType getType() {
        return CommandType.NEWFILENAME;
    }
}
