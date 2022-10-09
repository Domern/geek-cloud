package demo1;


import javafx.scene.control.TextField;
import model.*;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField nameClientField;
    public TextField nameServerField;
    private String currentDirectory;
    private File selectFile;

    private Network<ObjectDecoderInputStream, ObjectEncoderOutputStream> network;

    private Socket socket;

    private boolean needReadMessages = true;

    private DaemonThreadFactory factory;

    public void sengToClient(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        network.getOutputStream().writeObject(new FileRequest(fileName));
    }

    public void sendToServer(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        network.getOutputStream().writeObject(new FileMessage(Path.of(currentDirectory).resolve(fileName)));
    }
    public void addServerFileList(ActionEvent actionEvent) {
        try {
            network.getOutputStream().writeObject(new GiveList(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {

        try {
            while (needReadMessages) {
                CloudMessage message = (CloudMessage) network.getInputStream().readObject();
                if (message instanceof FileMessage fileMessage) {
                    Files.write(Path.of(currentDirectory).resolve(fileMessage.getFileName()), fileMessage.getBytes());
                    Platform.runLater(() -> fillView(clientView, getFiles(currentDirectory)));
                } else if (message instanceof ListMessage listMessage) {
                    Platform.runLater(() -> fillView(serverView, listMessage.getFiles()));
                }
            }
        } catch (Exception e) {
            System.err.println("Server off");
            e.printStackTrace();
        }
    }

    private void initNetwork() {
        try {
            socket = new Socket("localhost", 8100);
            network = new Network<>(
                    new ObjectDecoderInputStream(socket.getInputStream()),
                    new ObjectEncoderOutputStream(socket.getOutputStream())
            );

            factory.getThread(this::readMessages, "cloud-client-read-thread")
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        needReadMessages = true;
        factory = new DaemonThreadFactory();
        initNetwork();
        setCurrentDirectory("files-client");
        fillView(clientView, getFiles(currentDirectory));
        clientView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = clientView.getSelectionModel().getSelectedItem();
                File selectedFile = new File(currentDirectory + "/" + selected);
                if (selectedFile.isDirectory()) {
                    setCurrentDirectory(currentDirectory + "/" + selected);
                }
            }
        });
        serverView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selected = serverView.getSelectionModel().getSelectedItem();
                try {
                    network.getOutputStream().writeObject(new GiveList(selected));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setCurrentDirectory(String directory) {
        currentDirectory = directory;
        fillView(clientView, getFiles(currentDirectory));
    }

    private void fillView(ListView<String> view, List<String> data) {
        view.getItems().clear();
        view.getItems().addAll(data);
    }

    private List<String> getFiles(String directory) {
        // file.txt 125 b
        // dir [DIR]
        File dir = new File(directory);
        if (dir.isDirectory()) {
            String[] list = dir.list();
            if (list != null) {
                List<String> files = new ArrayList<>(Arrays.asList(list));
                files.add(0, "..");
                return files;
            }
        }
        return List.of();
    }


    public void deleteFileOnClient(ActionEvent actionEvent) throws IOException {
        String filename=clientView.getSelectionModel().getSelectedItem();
        Files.delete(Path.of(currentDirectory,filename).normalize());
        fillView(clientView, getFiles(currentDirectory));
    }

    public void newFilenameOnClient(ActionEvent actionEvent) throws IOException {
        String lastFilename=clientView.getSelectionModel().getSelectedItem();
        String newFileName=nameClientField.getText();
        if(newFileName.length()>0){
            Files.copy(Path.of(currentDirectory,lastFilename).normalize(),Path.of(currentDirectory,newFileName).normalize());
            Files.delete(Path.of(currentDirectory,lastFilename).normalize());
            fillView(clientView, getFiles(currentDirectory));
        }
    }

    public void deleteFileOnServer(ActionEvent actionEvent) throws IOException {
        String deleteFilename=serverView.getSelectionModel().getSelectedItem();
        network.getOutputStream().writeObject(new DeleteFile(deleteFilename));
    }

    public void newFilenameOnServer(ActionEvent actionEvent) throws IOException {
        String lastFileName=serverView.getSelectionModel().getSelectedItem();
        String newFileName=nameServerField.getText();
        network.getOutputStream().writeObject(new NewFileName(lastFileName,newFileName));
    }
}