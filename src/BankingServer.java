
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class BankingServer {
    private static final int PORT = 65432;
    public static ConcurrentHashMap<String, Integer> accounts = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        System.out.println("\t\t Banking Server");
        System.out.println("\t\t====================\n\n");

        ServerSocket serverSocket = new ServerSocket(PORT);

        try {
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("Client is Connected............\n\n");

                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.start();
            }
        } finally {
            serverSocket.close();
        }
    }
}
