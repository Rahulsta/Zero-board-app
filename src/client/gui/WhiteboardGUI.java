package client.gui;

import client.connection.ClientNetwork;

import javax.swing.*;
import java.awt.*;

public class WhiteboardGUI extends JFrame {
    private CanvasPanel whiteboardCanvas;
    private ConnectionPanel connectionPanel;
    private ToolsPanel toolsPanel;
    private ChatPanel chatPanel;
    private ClientNetwork clientNetwork;

    public WhiteboardGUI() {
        setTitle("ZeroBoard");
        setSize(1200, 750);
        setMinimumSize(new Dimension(900, 600));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CANVAS
        whiteboardCanvas = new CanvasPanel();

        // CHAT PANEL
        chatPanel = new ChatPanel();

        // NETWORK CLIENT
        clientNetwork = new ClientNetwork(whiteboardCanvas, chatPanel);
        chatPanel.setClientNetwork(clientNetwork);

        // CONNECTION PANEL (Pass network client)
        connectionPanel = new ConnectionPanel(clientNetwork);

        // Pass network client to CanvasPanel if needed
        whiteboardCanvas.setClientNetwork(clientNetwork);

        // TOOLS PANEL
        toolsPanel = new ToolsPanel(whiteboardCanvas, clientNetwork);

        // MAIN CONTAINER
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 30, 30));
        add(mainPanel);

        // TOP BAR
        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);

        mainPanel.add(whiteboardCanvas, BorderLayout.CENTER);
        mainPanel.add(connectionPanel, BorderLayout.WEST);
        mainPanel.add(toolsPanel, BorderLayout.EAST);
        mainPanel.add(chatPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 55));
        panel.setBackground(new Color(25, 25, 25));

        JLabel title = new JLabel("  ZeroBoard");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JLabel status = new JLabel("LAN COLLABORATIVE WHITEBOARD  ");
        status.setForeground(new Color(150, 150, 150));
        status.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panel.add(title, BorderLayout.WEST);
        panel.add(status, BorderLayout.EAST);

        return panel;
    }
}