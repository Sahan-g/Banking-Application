
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class BankingServer {
    private static final int PORT = 65432;
    public static ConcurrentHashMap<String, Integer> accounts = new ConcurrentHashMap<>();

    private static int count=0;

    public static void main(String[] args) throws IOException {
        System.out.println("\t\t Banking Server");
        System.out.println("\t\t====================\n\n");

        ServerSocket serverSocket = new ServerSocket(PORT);

        try {
            while (true) {
                Socket client = serverSocket.accept();
                count= count+1;
                System.out.println("Client is Connected............\n\n");
                System.out.println("Current Clients  : " + count );

                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.start();
            }
        } finally {
            serverSocket.close();
            count = count-1;
            System.out.println("Current Clients  : " + count );
        }
    }
}
