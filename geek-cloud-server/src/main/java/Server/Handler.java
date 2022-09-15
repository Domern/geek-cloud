package Server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Handler implements Runnable {
    private File dir;
    private final DataOutputStream out;
    private final DataInputStream in;
    private String msg;

    public Handler(Socket socket) throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client connected");
        sendFileList();
        new Thread(() -> readServiceMsg()).start();

    }

    private void sendFileList() {
        dir = new File("files");
        String[] fileList = dir.list();
        try {
            out.writeUTF("#list#");
            out.writeLong(fileList.length);
            for (String s : fileList) {
                out.writeUTF(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readServiceMsg() {
        try {
            msg = in.readUTF();
            if (msg.equals("#send-file#")) {
                receivingFile();
            } else if (msg.startsWith("#add-file#")) {
                String fileName = msg.substring(10);
                        sendFile(fileName);
            } else if (msg.equals("#list#")) {
                sendFileList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receivingFile() {
        try {
            File file = new File("files/" + in.readUTF());
            int x = (int) in.readLong();
            byte[] buf = new byte[x];
            in.read(buf);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendFile(String fileName) throws IOException {
        File file = new File("files/" + fileName);
        try (FileInputStream fis = new FileInputStream(file)) {
            long sise = (int) file.length();
            byte[] buf = new byte[(int) sise];
            int x = fis.read(buf);
            out.writeUTF("#add-file#");
            out.writeUTF(file.getName());
            out.writeLong(sise);
            out.write(buf);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

    }
}

