package client;

import common.CryptoUtils;
import common.Protocol;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;

public class FileSender {

    public static void sendFile(String filePath, String serverHost, int port,
                                String clientPrivateKeyFile, String serverPublicKeyFile,
                                String cipherAlgorithm, String signatureAlgorithm) {
        try {
            byte[] fileBytes = CryptoUtils.readFileToBytes(filePath);
            PrivateKey clientPrivKey = CryptoUtils.loadPrivateKey(clientPrivateKeyFile);
            PublicKey serverPubKey = CryptoUtils.loadPublicKey(serverPublicKeyFile);

            byte[] encryptedFile = CryptoUtils.encrypt(fileBytes, serverPubKey, cipherAlgorithm);
            byte[] signature = CryptoUtils.signData(fileBytes, clientPrivKey, signatureAlgorithm);

            try (Socket socket = new Socket(serverHost, port);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                Protocol packet = new Protocol(encryptedFile, signature, cipherAlgorithm, signatureAlgorithm);
                out.writeObject(packet);
                out.flush();
                JOptionPane.showMessageDialog(null, "File sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Send error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}