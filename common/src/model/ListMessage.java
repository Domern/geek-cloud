package model;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ListMessage implements CloudMessage{
    public final List<String> files;

    public List<String> getFiles() {
        return files;
    }

    public ListMessage(Path path) throws IOException {
        this.files= Files.list(path) //Stream<Path>
        .map(p->p.getFileName().toString()) //Stream<String>
        .collect(Collectors.toList());


    }


    @Override
    public MessageType getType() {
        return MessageType.LIST;
    }
}
