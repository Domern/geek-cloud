package Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server=new ServerSocket(8991);
        System.out.println("Server started");
        while (true){
            Socket socket= server.accept();
            new Thread(new Handler(socket)).start();
        }
    }
}

