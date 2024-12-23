package ds.message;

import com.google.gson.Gson;

/**
 * ResponseMessage class
 *
 * This class encapsulates a response message from the server to the client in a blockchain system.
 * It uses the Gson library for JSON serialization and deserialization.
 *
 * Note: This code was generated with assistance from Perplexity AI.
 */
public class ResponseMessage {
    private String status;
    private String data;
    private long executionTime;
    /**
     * Constructs a new ResponseMessage with specified status, data, and execution time.
     *
     * @param status        A string indicating the status of the response (e.g., "success", "error").
     * @param data          A string containing the response data or message.
     * @param executionTime A long value representing the execution time of the operation in milliseconds.
     */
    public ResponseMessage(String status, String data, long executionTime) {
        this.status = status;
        this.data = data;
        this.executionTime = executionTime;
    }
    /**
     * Converts this ResponseMessage object to a JSON string.
     *
     * @return A JSON string representation of this ResponseMessage.
     */
    public String toJson() {
        return new Gson().toJson(this);
    }
    /**
     * Creates a ResponseMessage object from a JSON string.
     *
     * @param json The JSON string to parse.
     * @return A new ResponseMessage object created from the JSON data.
     */
    public static ResponseMessage fromJson(String json) {
        return new Gson().fromJson(json, ResponseMessage.class);
    }

    // Getters
    public String getStatus() { return status; }
    public String getData() { return data; }
    public long getExecutionTime() { return executionTime; }
}
