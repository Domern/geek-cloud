package com.geekbrains.client;

import com.geekbrains.core.model.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    public TextField nameClientField;
    public TextField nameServerField;
    private Path currentDir;
    public ListView<String> clientView;
    public ListView<String> serverView;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;

    public void upload(ActionEvent actionEvent) throws IOException {
        Path file=currentDir.resolve(clientView.getSelectionModel().getSelectedItem());
        if(!Files.isDirectory(file)){
            os.writeObject(new FileMessage(file));
        }
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequest(fileName));
        refreshView(getFilesInCurrentDir(),clientView);
    }

    private void refreshView(List<String> files, ListView<String> view) {
        Platform.runLater(() -> {
            view.getItems().clear();
            view.getItems().addAll(files);
        });
    }

    private List<String> getFilesInCurrentDir() {
        try {
            List<String> files = Files.list(currentDir)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());

            if (Files.isDirectory(currentDir)) {
                if (files != null) {
                    files.add(0, "..");
                    return files;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return List.of();
    }

    private String resolveType(Path p){
        if(Files.isDirectory(p)){
            return " [DIR]";
        }else return " -"+p.toFile().length()+" bytes";
    }

    private void read() {
        try {
            while (true) {
                AbstractMessage msg = (AbstractMessage) is.readObject();
                switch (msg.getType()) {
                    case FILE_LIST_RESPONSE:
                        refreshView(((FileListResponse) msg).getFiles(), serverView);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            currentDir = Paths.get(System.getProperty("user.home"));
            refreshView(getFilesInCurrentDir(), clientView);
            Socket socket = new Socket("localhost", 8200);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread t = new Thread(this::read);
            t.setDaemon(true);
            t.start();

            clientView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = clientView.getSelectionModel().getSelectedItem();
                    if (Files.isDirectory(currentDir.resolve(selected))) {
                        currentDir= currentDir.resolve(selected).normalize();
                        refreshView(getFilesInCurrentDir(),clientView);
                    }
                }
            });
            serverView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selected = serverView.getSelectionModel().getSelectedItem();
                    try {
                        os.writeObject(new GiveFileList(selected));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshServerFileList(ActionEvent actionEvent) throws IOException {
        os.writeObject(new GiveFileList(""));
    }

    public void deleteFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename=clientView.getSelectionModel().getSelectedItem();
        Files.delete(currentDir.resolve(filename));
        refreshView(getFilesInCurrentDir(),clientView);
    }

    public void newFilenameOnClient(ActionEvent actionEvent) throws IOException {
        String lastFilename=clientView.getSelectionModel().getSelectedItem();
        String newFileName=nameClientField.getText();
        if(newFileName.length()>0){
            Files.copy(currentDir.resolve(lastFilename),currentDir.resolve(newFileName));
            Files.delete(currentDir.resolve(lastFilename));
            refreshView(getFilesInCurrentDir(),clientView);
        }
    }

    public void deleteFileOnServer(ActionEvent actionEvent) throws IOException {
        String deleteFilename=serverView.getSelectionModel().getSelectedItem();
        os.writeObject(new DeleteFile(deleteFilename));
    }

    public void newFilenameOnServer(ActionEvent actionEvent) throws IOException {
        String lastFileName=serverView.getSelectionModel().getSelectedItem();
        String newFileName=nameServerField.getText();
        os.writeObject(new NewFileName(lastFileName,newFileName));
    }
}
