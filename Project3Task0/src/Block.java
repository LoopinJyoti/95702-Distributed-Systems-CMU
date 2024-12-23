import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: October 27, 2024
 *
 * This class represents a Block in a blockchain implementation.
 * Each Block contains an index, timestamp, transaction data, previous hash,
 * nonce, and difficulty level. The class provides methods for calculating
 * the block's hash, performing proof of work, and managing block properties.
 * It demonstrates core concepts of blockchain technology including
 * hash calculation, proof of work, and block structure.
 *
 * Note: This documentation was generated with assistance from Perplexity AI.
 */

public class Block {
    private int index;
    private Timestamp timestamp;
    private String data;
    private String previousHash;
    private BigInteger nonce;
    private int difficulty;

    /**
     * Constructs a new Block with the given parameters.
     *
     * @param index The position of the block in the chain
     * @param timestamp The time of the block's creation
     * @param data The transaction data contained in the block
     * @param difficulty The number of leading zeros required in the block's hash
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        this.nonce = BigInteger.ZERO;
        this.previousHash = "";
    }

    /**
     * Calculates the SHA-256 hash of the block's contents.
     *
     * @return A hexadecimal string representation of the block's hash
     */
    public String calculateHash() {
        String input = index + timestamp.toString() + data + previousHash + nonce.toString() + difficulty;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs the proof of work algorithm to find a hash with the required difficulty.
     *
     * @return A hash string that meets the difficulty requirement
     */
    public String proofOfWork() {
        String hash;
        do {
            nonce = nonce.add(BigInteger.ONE);
            hash = calculateHash();
        } while (!hash.substring(0, difficulty).equals("0".repeat(difficulty)));
        return hash;
    }

    /**
     * Gets the nonce value used in the proof of work.
     *
     * @return The nonce as a BigInteger
     */
    public BigInteger getNonce() {
        return nonce;
    }

    /**
     * Gets the difficulty level of the block.
     *
     * @return The difficulty as an integer
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the difficulty level of the block.
     *
     * @param difficulty The new difficulty level
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Provides a string representation of the block in JSON format.
     *
     * @return A JSON string representing the block's data
     */
    @Override
    public String toString() {
        return String.format(
                "{\"index\": %d, \"timestamp\": \"%s\", \"data\": \"%s\", \"previousHash\": \"%s\", \"nonce\": %d, \"difficulty\": %d}",
                index, timestamp, data, previousHash, nonce, difficulty
        );
    }

    /**
     * Sets the hash of the previous block in the chain.
     *
     * @param previousHash The hash of the previous block
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * Gets the hash of the previous block in the chain.
     *
     * @return The hash of the previous block
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Gets the index of the block in the chain.
     *
     * @return The index of the block
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index of the block in the chain.
     *
     * @param index The new index of the block
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Sets the timestamp of the block.
     *
     * @param timestamp The new timestamp for the block
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the timestamp of the block.
     *
     * @return The timestamp of the block
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the transaction data stored in the block.
     *
     * @return The transaction data as a string
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the transaction data for the block.
     *
     * @param data The new transaction data
     */
    public void setData(String data) {
        this.data = data;
    }
}
