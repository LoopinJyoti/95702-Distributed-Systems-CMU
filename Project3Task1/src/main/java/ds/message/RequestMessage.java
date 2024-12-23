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
public class RequestMessage {
    private int operation;
    private String data;
    private String difficulty;
    /**
     * Constructs a new RequestMessage with specified operation, data, and difficulty.
     *
     * @param operation  An integer representing the type of operation requested.
     * @param data       A string containing the data associated with the request.
     * @param difficulty An integer representing the mining difficulty for blockchain operations.
     */
    public RequestMessage(int operation, String data, String difficulty) {
        this.operation = operation;
        this.data = data;
        this.difficulty = difficulty;
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

    // Getters
    public int getOperation() { return operation; }
    public String getData() { return data; }
    public String getDifficulty() { return difficulty; }
}
