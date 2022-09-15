package demo1;

import eu.hansolo.tilesfx.Tile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private Net net;
    private File dir;
    public ListView<String> serverList;
    public ListView<String> clientList;


    private void readMessage() {
        try {
            while (true) {
                clientFileList();
                String command = net.readUtf();
                if (command.equals("#list#")) {
                    addFileListFromServer();
                }else if(command.equals("#add-file#")){
                    addFile();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addFileListFromServer() throws IOException {
        Long fileCount = net.reedLong();
        for (int i = 0; i < fileCount; i++) {
            String fileName = net.readUtf();
            serverList.getItems().addAll(fileName);
        }
    }



    void clientFileList(){
        clientList.getItems().clear();
        dir=new File("files-client");
        String[] files = dir.list();
        for (String s : files) {
            clientList.getItems().addAll(s);
        }

    }



    private void addFile(){
            File file = new File("files-client/" + net.readUtf());
            long x = net.reedLong();
            byte[] buf = net.read((int) x);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(buf);
            } catch (IOException e) {
                e.printStackTrace();

        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            net = new Net("localhost", 8991);
            Thread readThread = new Thread(this::readMessage);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void addFile(ActionEvent actionEvent) {
        String fileName=serverList.getFocusModel().getFocusedItem();
        System.out.println(fileName);
        net.writeUTF("#add-file#"+fileName);



    }

    public void sendToServer(ActionEvent actionEvent) {
    }
}