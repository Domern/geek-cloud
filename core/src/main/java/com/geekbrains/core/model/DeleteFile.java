package com.geekbrains.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteFile extends AbstractMessage {
    private final String deleteFileName;

    public DeleteFile(String deleteFileName) {
        this.deleteFileName = deleteFileName;
    }

    @Override
    public CommandType getType() {
        return null;
    }
}
