package ds.client;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * RSASignatureUtils class
 * Provides utility methods for RSA key generation, signing, and verification.
 */
public class RSASignatureUtils {
    private BigInteger e, d, n;

    public RSASignatureUtils() {
        generateKeys();
    }

    private void generateKeys() {
        Random rnd = new Random();
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("65537");
        d = e.modInverse(phi);
    }

    public String getPublicKeyE() {
        return e.toString();
    }

    public String getPublicKeyN() {
        return n.toString();
    }

    public String sign(String message) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digestBytes = md.digest(message.getBytes());

        BigInteger messageHash = new BigInteger(1, digestBytes); // Ensure positive integer
        return messageHash.modPow(d, n).toString(); // Signature
    }

    public boolean verify(String message, String signatureStr, String eStr, String nStr) throws Exception {
        BigInteger signature = new BigInteger(signatureStr);
        BigInteger ePubKey = new BigInteger(eStr);
        BigInteger nPubKey = new BigInteger(nStr);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digestBytes = md.digest(message.getBytes());

        BigInteger originalHash = new BigInteger(1, digestBytes); // Ensure positive integer
        BigInteger decryptedHash = signature.modPow(ePubKey, nPubKey);

        return originalHash.equals(decryptedHash);
    }

    public String computeClientId() throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = sha256.digest((e.toString() + n.toString()).getBytes());
        return new BigInteger(1, hashBytes).toString(16).substring(0, 40); // Last 20 bytes
    }
}
