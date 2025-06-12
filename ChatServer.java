import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5000;
    private static Set<PrintWriter> clientOutputs = new HashSet<>();
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Le serveur démarre...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter output;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                output = new PrintWriter(socket.getOutputStream(), true);

                output.println("Entrez votre nom :");
                username = input.readLine();
                System.out.println(username + " s'est connecté.");

                synchronized (clients) {
                    clients.put(username, output);
                    clientOutputs.add(output);
                }

                broadcast(username + " a rejoint le chat.", null);

    
                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println(username + ": " + message);
                    broadcast(username + ": " + message, output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (username != null) {
                    synchronized (clients) {
                        clients.remove(username);
                        clientOutputs.remove(output);
                    }
                    broadcast(username + " a quitté le chat.", null);
                    System.out.println(username + " s'est déconnecté.");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void broadcast(String message, PrintWriter sender) {
        synchronized (clientOutputs) {
            for (PrintWriter writer : clientOutputs) {
                if (writer != sender) {
                    writer.println(message);
                }
            }
        }
    }
}