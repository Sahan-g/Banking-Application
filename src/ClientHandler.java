// ClientHandler.java

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            out.println("Welcome to the Banking Application");

            String command;
            while ((command = in.readLine()) != null) {
                String output = processCommand(command);
                out.println(output);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                client.close();
            } catch (IOException e) {
                System.err.println("Could not close streams or socket: " + e.getMessage());
            }
        }
    }

    private String processCommand(String command) {
        String[] parts = command.split(",");
        String cmdType = parts[0];
        String accountId = parts.length > 1 ? parts[1] : null;
        int amount;

        switch (cmdType) {
            case "CREATE":
                BankingServer.accounts.putIfAbsent(accountId, 0);
                return "Account " + accountId + " created successfully.";
            case "DEPOSIT":
                amount = Integer.parseInt(parts[2]);
                BankingServer.accounts.computeIfPresent(accountId, (key, val) -> val + amount);
                return "Deposited " + amount + " to account " + accountId;
            case "WITHDRAW":
                amount = Integer.parseInt(parts[2]);
                return BankingServer.accounts.computeIfPresent(accountId, (key, val) -> {
                    if (val >= amount) {
                        return val - amount;
                    } else {
                        out.println("Insufficient funds.");
                        return val;
                    }
                }).toString();
            case "BALANCE":
                int balance = BankingServer.accounts.getOrDefault(accountId, -1);
                if (balance >= 0) {
                    return "Account balance of " + accountId + " is " + balance;
                } else {
                    return "Account does not exist.";
                }
            default:
                return "Invalid command.";
        }
    }
}
