package server;

import common.CryptoUtils;

import javax.swing.*;
import java.awt.*;
import java.security.KeyPair;

public class ServerGUI {
    private JTextField portField = new JTextField("8080", 10);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }

    public ServerGUI() {
        ensureKeysExist();
        createAndShowGUI();
    }

    private void ensureKeysExist() {
        try {
            new java.io.File("keys").mkdirs();
            if (!new java.io.File("keys/server_private.key").exists()) {
                KeyPair kp = CryptoUtils.generateKeyPair();
                CryptoUtils.saveKeyToFile(kp.getPrivate(), "keys/server_private.key");
                CryptoUtils.saveKeyToFile(kp.getPublic(), "keys/server_public.key");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Server key generation error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Server: Secure File Transfer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 20));
        frame.setPreferredSize(new Dimension(400, 140));

        JLabel portLabel = new JLabel("Port:");
        portField.setPreferredSize(new Dimension(120, 28));
        portField.setFont(portField.getFont().deriveFont(Font.PLAIN, 14f));

        JButton startButton = new JButton("Start Server");
        startButton.setPreferredSize(new Dimension(140, 35));
        startButton.setFont(startButton.getFont().deriveFont(Font.BOLD, 14f));
        startButton.addActionListener(e -> {
            try {
                int port = Integer.parseInt(portField.getText());
                FileReceiver receiver = new FileReceiver(port,
                        "keys/server_private.key",
                        "keys/client_public.key");
                receiver.startServer();
                JOptionPane.showMessageDialog(frame, "Server started on port " + port);
                startButton.setEnabled(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid port number!");
            }
        });

        frame.add(portLabel);
        frame.add(portField);
        frame.add(startButton);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}