package common;

public class SecurityConfig {
    public static final int KEY_SIZE = 2048;

    // Поддерживаемые алгоритмы (для GUI)
    public static final String[] CIPHER_TRANSFORMATIONS = {
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
            "RSA/ECB/PKCS1Padding"
    };

    public static final String[] SIGNATURE_ALGORITHMS = {
            "SHA256withRSA",
            "SHA384withRSA",
            "SHA512withRSA"
    };
}