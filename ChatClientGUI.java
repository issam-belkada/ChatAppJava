import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClientGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private PrintWriter output;
    private String username;

    public ChatClientGUI() {
        setTitle("Client de Chat");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(245, 245, 245));
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Envoyer");

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        inputField.addActionListener(e -> sendMessage(inputField,chatArea));
        sendButton.addActionListener(e -> sendMessage(inputField,chatArea));
       

        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 5000);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            username = JOptionPane.showInputDialog(this, input.readLine(), "Nom d'utilisateur", JOptionPane.PLAIN_MESSAGE);
            if (username == null || username.trim().isEmpty()) {
                username = "Anonyme";
            }
            output.println(username);
            chatArea.append("Vous avez rejoint le chat en tant que " + username + ".\n");

        
            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = input.readLine()) != null) {
                        chatArea.append(serverMessage + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Connexion perdue.\n");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion au serveur", "Erreur", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    private void sendMessage(JTextField inputField2,JTextArea area) {
        String message = inputField2.getText();

            output.println(message);
            area.append(username + " : " + message + "\n");
           
            inputField2.setText("");
            inputField2.setText("");
            area.validate();
       
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClientGUI client = new ChatClientGUI();
            client.setVisible(true);
        });
    }
}