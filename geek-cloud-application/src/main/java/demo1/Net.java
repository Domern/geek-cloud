package demo1;

import javax.xml.crypto.Data;
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

    public DataOutputStream outputStream(){
        return out;
    }

    public DataInputStream inputStream()  {
       return in;
    }

}
