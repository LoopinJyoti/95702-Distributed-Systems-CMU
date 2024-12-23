package ds.message;


import com.google.gson.Gson;

/**
 * RequestMessage class
 *
 * This class encapsulates a request message from the client to the server in a blockchain system.
 * It uses the Gson library for JSON serialization and deserialization.
 *
 * Note: This code was generated with assistance from Perplexity AI.
 */
import com.google.gson.Gson;

/**
 * RequestMessage class
 * Encapsulates a signed request from the client to the server.
 */

import com.google.gson.Gson;

public class RequestMessage {
    private int operation;
    private String data;
    private int difficulty;
    private String clientId;
    private String publicKeyE;
    private String publicKeyN;
    private String signature;

    public RequestMessage(int operation,
                          String data,
                          int difficulty,
                          String clientId,
                          String publicKeyE,
                          String publicKeyN,
                          String signature) {
        this.operation = operation;
        this.data = data;
        this.difficulty = difficulty;
        this.clientId = clientId;
        this.publicKeyE = publicKeyE;
        this.publicKeyN = publicKeyN;
        this.signature = signature;
    }

    /**
     * Converts this RequestMessage object to a JSON string.
     *
     * @return A JSON string representation of this RequestMessage.
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Creates a RequestMessage object from a JSON string.
     *
     * @param json The JSON string to parse.
     * @return A new RequestMessage object created from the JSON data.
     */
    public static RequestMessage fromJson(String json) {
        return new Gson().fromJson(json, RequestMessage.class);
    }

    // Getters for all fields
    public int getOperation() { return operation; }
    public String getData() { return data; }
    public int getDifficulty() { return difficulty; }
    public String getClientId() { return clientId; }
    public String getPublicKeyE() { return publicKeyE; }
    public String getPublicKeyN() { return publicKeyN; }
    public String getSignature() { return signature; }
}

