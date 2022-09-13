package demo1;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class Net {
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Socket socket;

    private final String host;
    private final int port;


    public Net(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

        socket = new Socket(host, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public Long reedLong(){
        try {
           long l=in.readLong();
           return l;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String readUtf()  {
        try {
          String s=in.readUTF();
          return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public byte[] read(int x){

            byte[] buf = new byte[x];
        try {
            in.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }
public void writeUTF(String s){
    try {
        out.writeUTF(s);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public void writeLong(Long l){
    try {
        out.writeLong(l);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public void write(byte[] b){
    try {
        out.write(b);
    } catch (IOException e) {
        e.printStackTrace();
    }
}



}
