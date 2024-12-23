/*
 * CLASS LEVEL DOCUMENTATION
 *
 * Package: ds
 *
 * AUTHOR: JYOTI KHANCHANDANI
 *
 * The `Request` class is a data structure used to encapsulate a request in a distributed system.
 * It is designed to carry information related to an operation to be performed, such as the type of operation,
 * the values involved, and the number of iterations to be performed. This class supports multiple constructors
 * for flexibility, enabling the creation of requests with varying levels of detail depending on the context.
 *
 * The `Request` class can be used in client-server communication where specific instructions need to be passed
 * to a server for execution. For instance, it may be used to specify options for computations or configurations.
 * The class includes getter methods for each attribute and setters for optional fields like `values`.
 */

package ds; // Specify the package for this class.

import java.util.List; // Import for using List collections.

/**
 * The `Request` class encapsulates a request with operation details such as option type, values, and iterations.
 */
public class Request { // Class definition for `Request`.
    int option; // Variable to store the option type or operation code (e.g., computation type).
    List<Integer> values; // List of integers to hold values related to the request (e.g., numbers to be processed).
    int iterations; // Variable to store the number of iterations for a specific operation.

    /**
     * Getter method to retrieve the option value.
     *
     * @return The option type or operation code.
     */
    public int getOption() {
        return option; // Return the option type.
    }

    /**
     * Getter method to retrieve the list of values.
     *
     * @return A List of integers containing values associated with the request.
     */
    public List<Integer> getValues() {
        return values; // Return the list of values.
    }

    /**
     * Getter method to retrieve the number of iterations.
     *
     * @return The number of iterations for the requested operation.
     */
    public int getIterations() {
        return iterations; // Return the number of iterations.
    }

    /**
     * Constructor to initialize a Request with option, values, and iterations.
     *
     * @param option An integer representing the option type or operation code.
     * @param values A List of integers representing values associated with the request.
     * @param iterations An integer indicating the number of iterations for the operation.
     */
    public Request(int option, List<Integer> values, int iterations) {
        this.option = option; // Set the option type.
        this.values = values; // Set the values list.
        this.iterations = iterations; // Set the number of iterations.
    }

    /**
     * Constructor to initialize a Request with only an option.
     *
     * @param option An integer representing the option type or operation code.
     */
    public Request(int option) {
        this.option = option; // Set the option type.
    }

    /**
     * Setter method to set or update the list of values.
     *
     * @param values A List of integers to set as the values for the request.
     */
    public void setValues(List<Integer> values) {
        this.values = values; // Set the values list.
    }
}
