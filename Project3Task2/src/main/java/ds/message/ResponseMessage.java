package ds.message;

import com.google.gson.*;

/**
 * ResponseMessage class
 *
 * This class encapsulates a response message from the server to the client in a blockchain system.
 * It uses the Gson library for JSON serialization and deserialization.
 *
 * Note: This code was generated with assistance from Perplexity AI.
 */

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class ResponseMessage {
    private String status;
    private Object data; // Change data type to Object to allow both strings and arrays
    private long executionTime;

    public ResponseMessage(String status, Object data, long executionTime) {
        this.status = status;
        this.data = data;
        this.executionTime = executionTime;
    }

    // Convert to JSON
    public String toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", status);

        if (data instanceof JsonArray) {
            jsonObject.add("data", (JsonArray) data);
        } else {
            jsonObject.addProperty("data", data.toString());
        }

        jsonObject.addProperty("executionTime", executionTime);

        return new Gson().toJson(jsonObject);
    }

    // Create from JSON
    public static ResponseMessage fromJson(String json) {
        return new Gson().fromJson(json, ResponseMessage.class);
    }

    // Getters for all fields
    public String getStatus() { return status; }
    public Object getData() { return data; }
    public long getExecutionTime() { return executionTime; }
}
