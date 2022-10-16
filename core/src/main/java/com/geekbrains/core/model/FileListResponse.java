package com.geekbrains.core.model;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class FileListResponse extends AbstractMessage {
private List<String> files;

    public FileListResponse(Path path) throws IOException {
        files=Files.list(path)
                .map(p->p.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE_LIST_RESPONSE;
    }
}
