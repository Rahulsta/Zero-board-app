package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 5050;
    private static final List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("[Server] ZeroBoard TCP Server starting on port " + PORT + "...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("[Server] Client connected from: " + socket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                synchronized (clients) {
                    clients.add(clientHandler);
                }
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("[Server] Server exception: " + e.getMessage());
        }
    }

    public static void broadcast(Object message, ClientHandler excludeClient) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != excludeClient) {
                    client.send(message);
                }
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler) {
        synchronized (clients) {
            clients.remove(clientHandler);
            System.out.println("[Server] Client disconnected.");
        }
    }

    public static void broadcastUserList() {
        List<String> usernames = new ArrayList<>();
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getUsername() != null) {
                    usernames.add(client.getUsername());
                }
            }
        }
        broadcast(usernames, null);
    }
}