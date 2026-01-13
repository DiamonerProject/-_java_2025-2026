package client;

import common.CryptoUtils;
import common.SecurityConfig;

import javax.swing.*;
import java.awt.*;
import java.security.KeyPair;

public class ClientGUI {
    private JTextField filePathField = new JTextField(30);
    private JTextField serverHostField = new JTextField("localhost", 20);
    private JTextField serverPortField = new JTextField("8080", 8);
    private JComboBox<String> cipherCombo;
    private JComboBox<String> signatureCombo;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }

    public ClientGUI() {
        ensureKeysExist();
        createAndShowGUI();
    }

    private void ensureKeysExist() {
        try {
            new java.io.File("keys").mkdirs();
            if (!new java.io.File("keys/client_private.key").exists()) {
                KeyPair kp = CryptoUtils.generateKeyPair();
                CryptoUtils.saveKeyToFile(kp.getPrivate(), "keys/client_private.key");
                CryptoUtils.saveKeyToFile(kp.getPublic(), "keys/client_public.key");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Key generation error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Client: Secure File Transfer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filePanel.add(new JLabel("File to send:"));
        filePathField.setEditable(false);
        filePathField.setPreferredSize(new Dimension(300, 26));
        filePanel.add(Box.createHorizontalStrut(10));
        filePanel.add(filePathField);
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        filePanel.add(Box.createHorizontalStrut(10));
        filePanel.add(browseButton);
        mainPanel.add(filePanel);
        mainPanel.add(Box.createVerticalStrut(12));

        JPanel hostPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hostPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hostPanel.add(new JLabel("Server host:"));
        hostPanel.add(Box.createHorizontalStrut(10));
        serverHostField.setPreferredSize(new Dimension(200, 26));
        hostPanel.add(serverHostField);
        mainPanel.add(hostPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        portPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        portPanel.add(new JLabel("Server port:"));
        portPanel.add(Box.createHorizontalStrut(10));
        serverPortField.setPreferredSize(new Dimension(80, 26));
        portPanel.add(serverPortField);
        mainPanel.add(portPanel);
        mainPanel.add(Box.createVerticalStrut(12));

        JPanel cipherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cipherPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cipherPanel.add(new JLabel("Encryption algorithm:"));
        cipherPanel.add(Box.createHorizontalStrut(10));
        cipherCombo = new JComboBox<>(SecurityConfig.CIPHER_TRANSFORMATIONS);
        cipherCombo.setPreferredSize(new Dimension(380, 26));
        cipherPanel.add(cipherCombo);
        mainPanel.add(cipherPanel);
        mainPanel.add(Box.createVerticalStrut(12));

        JPanel sigPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sigPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sigPanel.add(new JLabel("Signature algorithm:"));
        sigPanel.add(Box.createHorizontalStrut(10));
        signatureCombo = new JComboBox<>(SecurityConfig.SIGNATURE_ALGORITHMS);
        signatureCombo.setPreferredSize(new Dimension(380, 26));
        sigPanel.add(signatureCombo);
        mainPanel.add(sigPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        JButton sendButton = new JButton("Send File");
        sendButton.setFont(sendButton.getFont().deriveFont(Font.BOLD, 14f));
        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendButton.addActionListener(e -> {
            String filePath = filePathField.getText().trim();
            if (filePath.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please select a file!");
                return;
            }
            try {
                int port = Integer.parseInt(serverPortField.getText());
                String cipherAlg = (String) cipherCombo.getSelectedItem();
                String sigAlg = (String) signatureCombo.getSelectedItem();
                FileSender.sendFile(
                        filePath,
                        serverHostField.getText().trim(),
                        port,
                        "keys/client_private.key",
                        "keys/server_public.key",
                        cipherAlg,
                        sigAlg
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid port number!");
            }
        });

        mainPanel.add(sendButton);

        frame.add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        frame.pack();
        frame.setMinimumSize(new Dimension(700, 380));
        frame.setLocationRelativeTo(null);ё
        frame.setVisible(true);
    }
}