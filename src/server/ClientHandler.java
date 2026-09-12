package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import client.model.ChatMessage;
import client.model.Line;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final List<ClientHandler> clients;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username = "Unknown";

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            // First message from client is the username handshake
            Object firstMsg = in.readObject();
            if (firstMsg instanceof String) {
                this.username = (String) firstMsg;
                System.out.println("[Server] User registered: " + this.username);
                Server.broadcastUserList(); // Broadcast updated list to everyone
            }

            while (true) {
                Object received = in.readObject();
                if (received instanceof Line) {
                    Server.broadcast(received, this);
                } else if (received instanceof ChatMessage) {
                    Server.broadcast(received, this);
                }
            }
        } catch (EOFException e) {
            // Normal client disconnect
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Server] Error processing client stream: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    public void send(Object message) {
        try {
            out.writeObject(message);
            out.flush();
            out.reset(); // Prevents ObjectOutputStream caching old instance headers
        } catch (IOException e) {
            System.err.println("[Server] Failed to send payload to peer: " + e.getMessage());
        }
    }

    private void closeConnection() {
        Server.removeClient(this);
        Server.broadcastUserList(); // Broadcast updated user list when someone leaves
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignored
        }
    }
}