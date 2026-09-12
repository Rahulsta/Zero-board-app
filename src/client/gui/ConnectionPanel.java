package client.gui;

import client.connection.ClientNetwork;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConnectionPanel extends JPanel {

    private final JTextField ipField;
    private final JTextField portField;
    private final JTextField usernameField;
    private final JButton connectButton;
    private final JLabel connectionStatus;
    private final DefaultListModel<String> usersModel;
    private final JList<String> usersList;
    private final String ip = "127.0.0.1";
    private final String portNum = "5050";

    private final ClientNetwork clientNetwork;

    public ConnectionPanel(ClientNetwork clientNetwork) {
        this.clientNetwork = clientNetwork;

        setPreferredSize(new Dimension(220, 0));
        setBackground(new Color(35, 35, 35));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // TITLE
        JLabel title = new JLabel("CONNECTION");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(title);
        add(Box.createVerticalStrut(20));

        // SERVER IP
        add(createLabel("Server IP"));
        ipField = new JTextField(ip);
        add(ipField);
        add(Box.createVerticalStrut(10));

        // PORT
        add(createLabel("Port"));
        portField = new JTextField(portNum);
        add(portField);
        add(Box.createVerticalStrut(10));

        // USERNAME
        add(createLabel("Username"));
        usernameField = new JTextField("User" + (int)(Math.random() * 1000));
        add(usernameField);
        add(Box.createVerticalStrut(15));

        // CONNECT BUTTON
        connectButton = new JButton("Connect");
        connectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(connectButton);
        add(Box.createVerticalStrut(15));

        // STATUS
        connectionStatus = new JLabel("● Disconnected");
        connectionStatus.setForeground(new Color(220, 80, 80));
        add(connectionStatus);
        add(Box.createVerticalStrut(25));

        // USERS
        JLabel usersTitle = new JLabel("CONNECTED USERS");
        usersTitle.setForeground(Color.WHITE);
        usersTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        add(usersTitle);
        add(Box.createVerticalStrut(8));

        usersModel = new DefaultListModel<>();
        usersList = new JList<>(usersModel);
        usersList.setBackground(new Color(45, 45, 45));
        usersList.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(usersList);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(scrollPane);

        usersModel.addElement("You");

        // PASS STATUS LABEL TO NETWORK CLIENT
        clientNetwork.setStatusLabel(connectionStatus);
        // Pass this connection panel to the network client
        clientNetwork.setConnectionPanel(this);

        // CONNECT ACTION
        connectButton.addActionListener(e -> {
            String serverIp = ipField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            String username = usernameField.getText().trim();

            if (clientNetwork.isConnected()) {
                clientNetwork.disconnect();
                connectButton.setText("Connect");
            } else {
                boolean success = clientNetwork.connect(serverIp, port, username);
                if (success) {
                    connectButton.setText("Disconnect");
                }
            }
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(180, 180, 180));
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public void updateUsersList(java.util.List<String> users) {
        SwingUtilities.invokeLater(() -> {
            usersModel.clear();
            for (String user : users) {
                usersModel.addElement(user);
            }
        });
    }
}