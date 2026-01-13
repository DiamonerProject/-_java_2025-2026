package common;

import java.io.Serializable;

public class Protocol implements Serializable {
    private static final long serialVersionUID = 1L;

    private byte[] encryptedFile;
    private byte[] digitalSignature;
    private String cipherAlgorithm;
    private String signatureAlgorithm;

    public Protocol(byte[] encryptedFile, byte[] digitalSignature,
                    String cipherAlgorithm, String signatureAlgorithm) {
        this.encryptedFile = encryptedFile;
        this.digitalSignature = digitalSignature;
        this.cipherAlgorithm = cipherAlgorithm;
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public byte[] getEncryptedFile() { return encryptedFile; }
    public byte[] getDigitalSignature() { return digitalSignature; }
    public String getCipherAlgorithm() { return cipherAlgorithm; }
    public String getSignatureAlgorithm() { return signatureAlgorithm; }
}