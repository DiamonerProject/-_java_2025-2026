package server;

import common.CryptoUtils;
import common.Protocol;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class FileReceiver {

    private final int port;
    private final String serverPrivateKeyFile;
    private final String clientPublicKeyFile;

    public FileReceiver(int port, String serverPrivateKeyFile, String clientPublicKeyFile) {
        this.port = port;
        this.serverPrivateKeyFile = serverPrivateKeyFile;
        this.clientPublicKeyFile = clientPublicKeyFile;
    }

    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server started on port " + port);
                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                         ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                        Protocol packet = (Protocol) in.readObject();
                        processReceivedFile(packet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Server error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void processReceivedFile(Protocol packet) {
        SwingUtilities.invokeLater(() -> {
            try {
                PrivateKey serverPrivKey = CryptoUtils.loadPrivateKey(serverPrivateKeyFile);
                PublicKey clientPubKey = CryptoUtils.loadPublicKey(clientPublicKeyFile);

                String cipherAlg = packet.getCipherAlgorithm();
                String sigAlg = packet.getSignatureAlgorithm();

                byte[] decryptedFile = CryptoUtils.decrypt(packet.getEncryptedFile(), serverPrivKey, cipherAlg);

                if (!CryptoUtils.verifySignature(decryptedFile, packet.getDigitalSignature(), clientPubKey, sigAlg)) {
                    JOptionPane.showMessageDialog(null, "Digital signature is INVALID! File rejected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String savePath = "received_" + System.currentTimeMillis() + ".bin";
                CryptoUtils.writeBytesToFile(decryptedFile, savePath);
                JOptionPane.showMessageDialog(null, "File received and saved as:\n" + savePath, "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Processing error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}