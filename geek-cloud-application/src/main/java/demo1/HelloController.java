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
    public ListView<String> clientView;
    public ListView<String> serverView;
    private Net net;
    private File dir;

    private void readMessage() {
        try {
            while (true) {
                String command = net.inputStream().readUTF();
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
        Long fileCount = net.inputStream().readLong();
        for (int i = 0; i < fileCount; i++) {
            String fileName = net.inputStream().readUTF();
            serverView.getItems().addAll(fileName);
        }
    }



    void clientFileList(){
        clientView.getItems().clear();
        dir=new File("files-client");
        String[] files = dir.list();
        for (String s : files) {
            clientView.getItems().addAll(s);
        }

    }



    private void addFile(){
        try{
            File file = new File("files-client/" + net.inputStream().readUTF());
            int x = (int) net.inputStream().readLong();
            byte[] buf = new byte[x];
            net.inputStream().read(buf);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
        }
    }catch (IOException e) {
            e.printStackTrace();
        }
    }


        @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
            try {
                net = new Net("localhost", 8991);
                Thread readThread = new Thread(this::readMessage);
                readThread.setDaemon(true);
                readThread.start();
                clientFileList();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    public void sendToServer(ActionEvent actionEvent) {
        String filename=clientView.getFocusModel().getFocusedItem();
        File file=new File("files-client/"+filename);
        long size= file.length();
        byte[] buf=new byte[(int) size];
        try (FileInputStream fis = new FileInputStream(file)) {
            int x=fis.read(buf);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
        net.outputStream().writeUTF("#send-file#");
        net.outputStream().writeUTF(filename);
        net.outputStream().writeLong(size);
        net.outputStream().write(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sengToClient(ActionEvent actionEvent) {
        String fileName=serverView.getFocusModel().getFocusedItem();
        System.out.println(fileName);
        try {
            net.outputStream().writeUTF("#add-file#"+fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
