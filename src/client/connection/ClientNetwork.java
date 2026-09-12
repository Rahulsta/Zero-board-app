package client.connection;

import client.gui.CanvasPanel;
import client.model.ChatMessage;
import client.gui.ChatPanel;
import client.gui.ConnectionPanel;
import client.model.Line;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientNetwork {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean connected = false;
    private ConnectionPanel connectionPanel;

    private final CanvasPanel canvasPanel;
    private final ChatPanel chatPanel;
    private JLabel statusLabel;

    public ClientNetwork(CanvasPanel canvasPanel, ChatPanel chatPanel) {
        this.canvasPanel = canvasPanel;
        this.chatPanel = chatPanel;
    }

    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
    }

    public boolean connect(String ip, int port, String username) {
        try {
            socket = new Socket(ip, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            connected = true;

            chatPanel.setUsername(username);

            // Send handshake/username
            out.writeObject(username);
            out.flush();

            if (statusLabel != null) {
                statusLabel.setText("● Connected");
                statusLabel.setForeground(new java.awt.Color(80, 220, 80));
            }

            chatPanel.appendMessage("Connected to server.");

            new Thread(this::listenForServerMessages).start();
            return true;
        } catch (IOException e) {
            if (statusLabel != null) {
                statusLabel.setText("● Connection Failed");
                statusLabel.setForeground(new java.awt.Color(220, 80, 80));
            }
            chatPanel.appendMessage("Connection failed.");
            return false;
        }
    }

    public void sendLine(Line line) {
        if (!connected) return;
        try {
            out.writeObject(line);
            out.flush();
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendChatMessage(ChatMessage message) {
        if (!connected) return;
        try {
            out.writeObject(message);
            out.flush();
            out.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForServerMessages() {
        try {
            while (connected) {
                Object received = in.readObject();
                if (received instanceof Line) {
                    Line line = (Line) received;
                    SwingUtilities.invokeLater(() -> {
                        canvasPanel.getLinesList().add(line);
                        canvasPanel.repaint();
                    });
                } else if (received instanceof ChatMessage) {
                    ChatMessage chatMsg = (ChatMessage) received;
                    chatPanel.appendMessage("[" + chatMsg.getSender() + "] " + chatMsg.getMessage());
                } else if (received instanceof java.util.List) {
                    // Handle incoming user list from server
                    @SuppressWarnings("unchecked")
                    java.util.List<String> users = (java.util.List<String>) received;
                    if (connectionPanel != null) {
                        connectionPanel.updateUsersList(users);
                    }
                }
            }
        } catch (Exception e) {
            disconnect();
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignore
        }
        if (statusLabel != null) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("● Disconnected");
                statusLabel.setForeground(new java.awt.Color(220, 80, 80));
                chatPanel.appendMessage("Disconnected from server.");
            });
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnectionPanel(ConnectionPanel connectionPanel) {
        this.connectionPanel = connectionPanel;
    }
}