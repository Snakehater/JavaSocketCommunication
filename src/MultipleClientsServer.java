import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultipleClientsServer {

    static final int PORT = 6969;

    public static void main(String args[]) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        int serverIdx = 0;

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            serverIdx++;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new ServerInterface(socket, "Server-" + serverIdx).start();
        }
    }
}

