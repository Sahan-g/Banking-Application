import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;

public class BankingClientGUI extends JFrame {
    private JTextField serverIPField;
    private JTextField serverPortField;
    private JButton connectButton;
    private JTextField accountIdField;
    private JTextField amountField;
    private JButton createButton;
    private JButton depositButton;
    private JButton withdrawButton;
    private JButton balanceButton;
    private JTextArea statusArea;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public BankingClientGUI() {
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        setTitle("Banking Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLayout(new BorderLayout());

        // Connection Panel
        JPanel connectionPanel = new JPanel();
        serverIPField = new JTextField("localhost", 10);
        serverPortField = new JTextField("65432", 5);
        connectButton = new JButton("Connect");
        connectButton.addActionListener(this::connectToServer);
        connectionPanel.add(new JLabel("Server IP:"));
        connectionPanel.add(serverIPField);
        connectionPanel.add(new JLabel("Port:"));
        connectionPanel.add(serverPortField);
        connectionPanel.add(connectButton);

        // Account Actions Panel
        JPanel actionsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        accountIdField = new JTextField(10);
        amountField = new JTextField(10);
        createButton = new JButton("Create Account");
        depositButton = new JButton("Deposit Money");
        withdrawButton = new JButton("Withdraw Money");
        balanceButton = new JButton("Check Balance");
        createButton.addActionListener(e -> sendCommand("CREATE," + accountIdField.getText()));
        depositButton.addActionListener(e -> sendCommand("DEPOSIT," + accountIdField.getText() + "," + amountField.getText()));
        withdrawButton.addActionListener(e -> sendCommand("WITHDRAW," + accountIdField.getText() + "," + amountField.getText()));
        balanceButton.addActionListener(e -> sendCommand("BALANCE," + accountIdField.getText()));
        actionsPanel.add(new JLabel("Account ID:"));
        actionsPanel.add(accountIdField);
        actionsPanel.add(new JLabel("Amount:"));
        actionsPanel.add(amountField);
        actionsPanel.add(createButton);
        actionsPanel.add(depositButton);
        actionsPanel.add(withdrawButton);
        actionsPanel.add(balanceButton);

        // Status Area
        statusArea = new JTextArea();
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);

        add(connectionPanel, BorderLayout.NORTH);
        add(actionsPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void connectToServer(ActionEvent e) {
        String serverIP = serverIPField.getText();
        int port = Integer.parseInt(serverPortField.getText());
        try {
            socket = new Socket(serverIP, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this::readServerResponse).start();
            statusArea.append("Connected to server at " + serverIP + ":" + port + "\n");
        } catch (IOException ioException) {
            statusArea.append("Failed to connect: " + ioException.getMessage() + "\n");
        }
    }

    private void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }

    private void readServerResponse() {
        String response;
        try {
            while ((response = in.readLine()) != null) {
                final String resp = response;
                SwingUtilities.invokeLater(() -> statusArea.append("Server: " + resp + "\n"));
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> statusArea.append("Server connection lost: " + e.getMessage() + "\n"));
        }
    }

    public static void main(String[] args) {
        new BankingClientGUI();
    }
}
