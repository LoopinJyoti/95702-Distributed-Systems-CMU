/*
 * CLASS LEVEL DOCUMENTATION
 *
 * AUTHOR: JYOTI KHANCHANDANI
 *
 * This program demonstrates a UDP server that manages a remote variable for multiple users. Each user has a unique ID and
 * can add to, subtract from, or get the current value of their variable stored on the server. The server accepts requests
 * from multiple clients, processes their operations, and maintains each user's variable independently using a TreeMap.
 *
 * The communication is handled using UDP packets. Each request packet from the client contains three integers (user ID, value,
 * operation type), and the server responds with the updated sum or the current value of the user's variable.
 * This program demonstrates basic server operations such as packet handling, multi-user management, and marshaling of
 * request data using `ByteBuffer`.
 *  Citation: Initial development of this code was influenced by generative AI guidance from Perplexity.
 */

import java.io.*; // Import for handling I/O operations.
import java.net.*; // Import for networking classes.
import java.nio.ByteBuffer; // Import for byte manipulation using buffers.
import java.util.TreeMap; // Import for TreeMap data structure to manage user sums.

public class RemoteVariableServerUDP { // Class definition for the server program.

    private static final int ADD = 1; // Operation code for adding a value.
    private static final int SUBTRACT = 2; // Operation code for subtracting a value.
    private static final int GET = 3; // Operation code for retrieving the current sum.
    private static DatagramSocket aSocket = null; // Socket to handle UDP communication.

    /**
     * Main method for the server application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String args[]) { // Main entry point for the server program.

        byte[] buffer = new byte[1000]; // Buffer for storing incoming data packets.
        TreeMap<Integer, Integer> userSums = new TreeMap<>(); // TreeMap to manage each user's sum by their ID.

        try { // Start of try block to handle socket and I/O exceptions.
            System.out.println("The UDP server is running"); // Print a message indicating that the server is running.

            System.out.print("Enter server port number to listen to: "); // Prompt user to enter the port number.
            BufferedReader portListen = new BufferedReader(new InputStreamReader(System.in)); // Read the port number from the console.
            int serverPort = Integer.parseInt(portListen.readLine()); // Convert the entered port number into an integer.
            aSocket = new DatagramSocket(serverPort); // Create a DatagramSocket bound to the specified port.

            while (true) { // Infinite loop to handle incoming client requests.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length); // Prepare a DatagramPacket to receive data.
                aSocket.receive(request); // Receive a request packet from a client.

                ByteBuffer wrapped = ByteBuffer.wrap(request.getData()); // Wrap the received byte array into a ByteBuffer.
                int userId = wrapped.getInt(); // Extract the user ID from the ByteBuffer.
                int value = wrapped.getInt(); // Extract the value from the ByteBuffer.
                int operation = wrapped.getInt(); // Extract the operation code from the ByteBuffer.

                int result = processRequest(userSums, userId, value, operation); // Process the request based on the operation type.

                String resultString = String.valueOf(result); // Convert the result into a string.
                byte[] resultBytes = resultString.getBytes(); // Convert the result string into a byte array.

                DatagramPacket reply = new DatagramPacket(resultBytes, resultBytes.length, request.getAddress(), request.getPort()); // Create a reply packet.
                aSocket.send(reply); // Send the reply packet back to the client.
            }
        } catch (SocketException e) { // Catch block for socket-related exceptions.
            System.out.println("Socket: " + e.getMessage()); // Print the exception message.
        } catch (IOException e) { // Catch block for I/O-related exceptions.
            System.out.println("IO: " + e.getMessage()); // Print the exception message.
        } finally { // Ensure the following block is executed whether or not an exception occurs.
            if (aSocket != null) aSocket.close(); // Close the socket if it was opened.
        }
    }

    /**
     * Processes the incoming request based on the user ID, value, and operation type.
     *
     * @param userSums TreeMap storing current sums for each user by their ID.
     * @param userId The ID of the user making the request.
     * @param value The value to be added or subtracted (ignored for GET operation).
     * @param operation The type of operation to be performed (ADD, SUBTRACT, or GET).
     * @return The updated sum after the operation.
     */
    private static int processRequest(TreeMap<Integer, Integer> userSums, int userId, int value, int operation) {
        int currentSum = userSums.getOrDefault(userId, 0); // Retrieve the current sum for the user, defaulting to 0 if not found.
        int newSum; // Variable to store the updated sum after the operation.

        switch (operation) { // Switch statement to handle different operation types.
            case ADD: // If the operation is ADD.
                newSum = add(currentSum, value); // Call the add method with the current sum and value.
                break;
            case SUBTRACT: // If the operation is SUBTRACT.
                newSum = subtract(currentSum, value); // Call the subtract method with the current sum and value.
                break;
            case GET: // If the operation is GET.
                System.out.println("User ID: " + userId + ", Current Sum: " + currentSum); // Print the current sum for the user.
                return currentSum; // Return the current sum.
            default: // If the operation code is invalid.
                System.out.println("Invalid operation received"); // Print an error message.
                return currentSum; // Return the current sum without modification.
        }

        userSums.put(userId, newSum); // Update the sum for the user in the TreeMap.
        System.out.println("User ID: " + userId + ", New Sum: " + newSum); // Print the updated sum for the user.
        return newSum; // Return the updated sum.
    }

    /**
     * Adds the given value to the current sum.
     *
     * @param currentSum The current sum of the user.
     * @param value The value to be added.
     * @return The new sum after addition.
     */
    private static int add(int currentSum, int value) { // Method to add a value to the current sum.
        System.out.println("Adding: " + value + " to " + currentSum); // Print the addition operation being performed.
        return currentSum + value; // Return the updated sum after addition.
    }

    /**
     * Subtracts the given value from the current sum.
     *
     * @param currentSum The current sum of the user.
     * @param value The value to be subtracted.
     * @return The new sum after subtraction.
     */
    private static int subtract(int currentSum, int value) { // Method to subtract a value from the current sum.
        System.out.println("Subtracting: " + value + " from " + currentSum); // Print the subtraction operation being performed.
        return currentSum - value; // Return the updated sum after subtraction.
    }
}
