/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: October 31, 2024
 * This utility class provides RSA-based digital signature functionalities.
 * * It includes methods to generate public/private key pairs, sign messages,
 * * and verify signatures. It uses SHA-256 for hashing messages before signing
 * * and allows for the public key to be retrieved as needed.
 *
 *
 *NOTE : This code is generated with assisstance from Perplexity AI.
 *
 */

package ds.server;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


public class RSASignatureUtils {
    private BigInteger e, d, n;

    public RSASignatureUtils() {
        generateKeys();
    }
    /**
     * Generates a public-private key pair using two random prime numbers.
     * The public exponent (e) is set to 65537, a common value for RSA.
     * The private exponent (d) is calculated as the modular inverse of e.
     */
    private void generateKeys() {
        Random rnd = new Random();
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("65537");
        d = e.modInverse(phi);  }

    /**
     * Returns the public exponent as a string.
     *
     * @return String representation of the public exponent.
     */
    public String getPublicKeyE() {
        return e.toString();
    }
    /**
     * Returns the public modulus as a string.
     *
     * @return String representation of the modulus.
     */
    public String getPublicKeyN() {
        return n.toString();
    }
    /**
     * Signs a message using the private key after hashing it with SHA-256.
     *
     * @param message The message to sign.
     * @return The digital signature as a string.
     * @throws Exception If SHA-256 is not available.
     */
    public String sign(String message) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digestBytes = md.digest(message.getBytes());

        BigInteger messageHash = new BigInteger(1, digestBytes); // Ensure positive integer
        return messageHash.modPow(d, n).toString(); // Signature
    }
    /**
     * Verifies a digital signature using a provided public key.
     *
     * @param message The original message.
     * @param signatureStr The signature to verify, as a string.
     * @param eStr The public exponent, as a string.
     * @param nStr The modulus, as a string.
     * @return true if the signature is valid, false otherwise.
     * @throws Exception If SHA-256 is not available.
     */
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
    /**
     * Computes a client ID based on the modulus and exponent.
     *
     * @return A unique identifier as a hexadecimal string.
     * @throws NoSuchAlgorithmException If SHA-256 is not available.
     */
    public String computeClientId() throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = sha256.digest((e.toString() + n.toString()).getBytes());
        return new BigInteger(1, hashBytes).toString(16).substring(0, 40); // Last 20 bytes
    }
}
