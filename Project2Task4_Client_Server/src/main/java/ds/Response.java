/*
 * CLASS LEVEL DOCUMENTATION
 *
 * Package: ds
 *
 * AUTHOR: JYOTI KHANCHANDANI
 *
 * This class, named `Response`, is a data structure used to encapsulate different types of responses
 * that may be generated and exchanged within a distributed system. It supports storing and managing
 * various types of information such as a 2D truth table, an error value, a status message, and a custom
 * message. The class provides multiple constructors to initialize instances based on the type of
 * information available. This flexibility allows for versatile use across different components of
 * the system.
 *
 * The `Response` class can serve in contexts where results of computations, validation status, or
 * operational messages need to be communicated between different parts of the system, such as between
 * servers and clients. It implements basic getters and setters to access and modify its fields.
 */

package ds; // Specify the package for this class.

import java.util.List; // Import for using List collections.

/**
 * The `Response` class encapsulates various response data, such as a 2D truth table, error values, and status messages.
 */
public class Response { // Class definition for `Response`.
    // 2D list to store a truth table. Each sublist represents a row of the truth table.
    List<List<Integer>> truthTable;

    // Variable to store an error value, which might represent computation errors or evaluation metrics.
    Double error;

    // Variable to store the status of the response, typically indicating success, failure, or other conditions.
    String status;

    // Variable to store custom messages that provide additional context or details about the response.
    String message;

    /**
     * Constructor to initialize the Response with a given truth table and error value.
     *
     * @param truthTable A 2D list representing a truth table.
     * @param error A Double value representing an error metric or computation result.
     */
    public Response(List<List<Integer>> truthTable, Double error) {
        this.truthTable = truthTable; // Set the truth table.
        this.error = error; // Set the error value.
    }

    /**
     * Constructor to initialize the Response with only a truth table.
     *
     * @param truthTable A 2D list representing a truth table.
     */
    public Response(List<List<Integer>> truthTable) {
        this.truthTable = truthTable; // Set the truth table.
    }

    /**
     * Constructor to initialize the Response with only an error value.
     *
     * @param error A Double value representing an error metric or computation result.
     */
    public Response(Double error) {
        this.error = error; // Set the error value.
    }

    /**
     * Constructor to initialize the Response with a status string.
     *
     * @param status A String representing the status of the response (e.g., "Success" or "Failure").
     */
    public Response(String status) {
        this.status = status; // Set the status value.
    }

    /**
     * Sets the truth table in the Response.
     *
     * @param truthTable A 2D list representing the truth table to be set.
     */
    public void setTruthTable(List<List<Integer>> truthTable) {
        this.truthTable = truthTable; // Set the truth table.
    }

    /**
     * Sets the error value in the Response.
     *
     * @param error A Double value representing the error to be set.
     */
    public void setError(Double error) {
        this.error = error; // Set the error value.
    }

    /**
     * Retrieves the truth table from the Response.
     *
     * @return A 2D list representing the truth table.
     */
    public List<List<Integer>> getTruthTable() {
        return truthTable; // Return the truth table.
    }

    /**
     * Retrieves the error value from the Response.
     *
     * @return A Double value representing the error.
     */
    public Double getError() {
        return error; // Return the error value.
    }

    /**
     * Retrieves the status from the Response.
     *
     * @return A String representing the status of the response.
     */
    public String getStatus() {
        return status; // Return the status.
    }

    /**
     * Retrieves the custom message from the Response.
     *
     * @return A String containing the custom message.
     */
    public String getMessage() {
        return message; // Return the custom message.
    }

    /**
     * Sets the status of the Response.
     *
     * @param status A String representing the status to be set.
     */
    public void setStatus(String status) {
        this.status = status; // Set the status.
    }

    /**
     * Sets a custom message in the Response.
     *
     * @param message A String containing the custom message to be set.
     */
    public void setMessage(String message) {
        this.message = message; // Set the custom message.
    }
}
