package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class CryptoUtils {

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(SecurityConfig.KEY_SIZE);
        return kpg.generateKeyPair();
    }

    public static void saveKeyToFile(Key key, String filename) throws IOException {
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        Files.write(Paths.get(filename), encoded.getBytes());
    }

    public static PrivateKey loadPrivateKey(String filename) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(filename)));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String filename) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(Files.readAllBytes(Paths.get(filename)));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    public static byte[] readFileToBytes(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    public static void writeBytesToFile(byte[] data, String filePath) throws IOException {
        Files.write(Paths.get(filePath), data);
    }

    // --- Гибкие методы с выбором алгоритмов ---

    public static byte[] encrypt(byte[] data, PublicKey publicKey, String cipherTransformation) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encryptedData, PrivateKey privateKey, String cipherTransformation) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherTransformation);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(encryptedData);
    }

    public static byte[] signData(byte[] data, PrivateKey privateKey, String signatureAlgorithm) throws Exception {
        Signature sig = Signature.getInstance(signatureAlgorithm);
        sig.initSign(privateKey);
        sig.update(data);
        return sig.sign();
    }

    public static boolean verifySignature(byte[] data, byte[] signature, PublicKey publicKey, String signatureAlgorithm) throws Exception {
        Signature sig = Signature.getInstance(signatureAlgorithm);
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signature);
    }
}