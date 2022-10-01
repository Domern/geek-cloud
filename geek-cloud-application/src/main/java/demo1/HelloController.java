package demo1;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import model.CloudMessage;
import model.FileMessage;
import model.FileRequest;
import model.ListMessage;

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
    private String currentDirectory;

    private NetWork<ObjectDecoderInputStream, ObjectEncoderOutputStream> network;

    private Socket socket;

    private boolean needReadMessages = true;


    public void sengToClient(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        network.getOutputStream().writeObject(new FileRequest(fileName));
    }

    public void sendToServer(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        network.getOutputStream().writeObject(new FileMessage(Path.of(currentDirectory).resolve(fileName)));
    }

    private void readMessages() {
        try {
            while (needReadMessages) {
                CloudMessage message = (CloudMessage) network.getInputStream().readObject();
                System.out.println(message);
                System.out.println("cloud");
                if (message instanceof FileMessage fileMessage) {
                    System.out.println("message");
                    Files.write(Path.of(currentDirectory).resolve(fileMessage.getFileName()), fileMessage.getBytes());
                    Platform.runLater(() -> fillView(clientView, getFiles(currentDirectory)));
                } else if (message instanceof ListMessage listMessage) {
                    System.out.println("list");
                    for (String file : listMessage.getFiles()) {
                        System.out.println(file);
                    }
                    Platform.runLater(() -> fillView(serverView, listMessage.getFiles()));
                }
            }
        } catch (Exception e) {
            System.err.println("Server off");
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            socket = new Socket("localhost", 8195);
            network=new NetWork<>(
                    new ObjectDecoderInputStream(socket.getInputStream()),
                    new ObjectEncoderOutputStream(socket.getOutputStream())
            );
            currentDirectory="files-client";
            fillView(clientView, getFiles(currentDirectory));
            Thread readThread = new Thread(this::readMessages);
            readThread.setDaemon(true);
            readThread.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void addServerFileList(ActionEvent actionEvent) {
    }
}