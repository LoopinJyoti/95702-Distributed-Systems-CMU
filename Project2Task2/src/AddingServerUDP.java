/**
 * Author: JYOTI KHANCHANDANI
 * Last Modified: 3rd October 2024
 *
 * This program implements a UDP server for an adding service.
 * The server listens for incoming UDP packets containing integers,
 * adds them to a running sum, and sends the updated sum back to the client.
 * It maintains separate sums for each client session based on unique identifiers.
 *
 * Key features of this UDP server:
 * 1. User-defined port: Prompts for the port number to listen on.
 * 2. Continuous operation: Runs in an infinite loop, processing requests until 'halt!' is received.
 * 3. Session management: Maintains a running sum for each client session using unique client identifiers.
 * 4. Separation of concerns: Arithmetic logic is encapsulated in the 'doAdd' method.
 * 5. Error handling: Manages SocketExceptions and IOExceptions.
 * 6. Graceful termination: Closes the socket when 'halt!' is received or on exceptions.
 *
 * The program demonstrates:
 * - UDP socket programming in Java for server-side applications
 * - Handling of incoming UDP packets and sending responses
 * - Session management to maintain state across client interactions
 * - Separation of network communication and business logic
 * - Basic error handling in network programming
 *
 * Usage:
 * 1. Run the program
 * 2. Enter the port number for the server to listen on when prompted
 * 3. The server will start listening for incoming UDP packets
 * 4. For each received integer, it will update the sum for that client and send it back
 * 5. The server will terminate when it receives 'halt!'
 *
 * Note: This server is designed to work with a corresponding UDP client
 * that sends integers for addition.
 *
 * @apiNote This implementation was assisted by ChatGPT (OpenAI, 2024) and Perplexity AI
 *           on October 3, 2024.
 */

import java.io.BufferedReader; // Import for reading input from the console.
import java.io.IOException; // Import for handling I/O exceptions.
import java.io.InputStreamReader; // Import for converting byte streams to character streams.
import java.net.DatagramPacket; // Import for creating UDP packets.
import java.net.DatagramSocket; // Import for creating UDP sockets.
import java.net.SocketException; // Import for handling exceptions related to socket creation.
import java.util.HashMap; // Import for creating a hash map to store client-specific data.
import java.util.Map; // Import for working with Map interfaces.

public class AddingServerUDP { // Define a class named AddingServerUDP for the UDP server.

    public static void main(String args[]) { // Main entry point of the program.
        DatagramSocket aSocket = null; // Declare a DatagramSocket to handle communication.
        byte[] buffer = new byte[1000]; // Create a byte buffer to store incoming data.
        Map<String, Integer> clientSums = new HashMap<>(); // Create a map to store sum values for each client.

        try { // Start of try block to handle socket and I/O exceptions.
            System.out.println("The UDP server is running"); // Inform the user that the server is running.

            System.out.print("Enter server port number to listen to: "); // Prompt user to enter a port number.
            BufferedReader portListen = new BufferedReader(new InputStreamReader(System.in)); // Read port number input.
            int serverPort = Integer.parseInt(portListen.readLine()); // Convert the input into an integer.
            aSocket = new DatagramSocket(serverPort); // Create a UDP socket on the specified port.

            while (true) { // Begin an infinite loop to listen for incoming client requests.
                DatagramPacket request = new DatagramPacket(buffer, buffer.length); // Prepare a DatagramPacket to receive data.
                aSocket.receive(request); // Receive data from a client.

                String clientKey = request.getAddress().toString() + ":" + request.getPort(); // Identify the client based on its IP and port.
                String requestString = new String(request.getData(), 0, request.getLength()).trim(); // Convert the received data into a string and trim extra spaces.

                if ("halt!".equals(requestString)) { // Check if the client wants to stop the server.
                    System.out.println("UDP Server side quitting"); // Print message indicating server shutdown.
                    break; // Exit the loop to terminate the server.
                }

                // Initialize sum for new clients.
                if (!clientSums.containsKey(clientKey)) { // If the client is new (not in the map).
                    System.out.println("New client connected. Sum reset to 0."); // Notify that a new client is connected.
                    clientSums.put(clientKey, 0); // Initialize the sum for the new client as 0.
                }

                int currentSum = clientSums.get(clientKey); // Get the current sum for the client.
                int requestInteger = Integer.parseInt(requestString); // Parse the client's message as an integer.
                currentSum = doAdd(currentSum, requestInteger); // Call the doAdd method to update the sum.
                clientSums.put(clientKey, currentSum); // Update the sum for the client in the map.
                System.out.println(clientSums); // Print the map of all clients and their sums.

                String sumString = String.valueOf(currentSum); // Convert the updated sum to a string.
                byte[] sumBytes = sumString.getBytes(); // Convert the string to a byte array for transmission.

                DatagramPacket reply = new DatagramPacket(sumBytes, sumBytes.length, request.getAddress(), request.getPort()); // Create a reply packet with the sum.
                aSocket.send(reply); // Send the reply packet back to the client.
            }
        } catch (SocketException e) { // Catch block for socket-related exceptions.
            System.out.println("Socket: " + e.getMessage()); // Print the socket error message.
        } catch (IOException e) { // Catch block for I/O-related exceptions.
            System.out.println("IO: " + e.getMessage()); // Print the I/O error message.
        } finally { // Ensure this block is executed whether or not an exception occurs.
            if (aSocket != null) aSocket.close(); // Close the socket if it was opened.
        }
    }

    private static int doAdd(int currentSum, int numberToAdd) { // Helper method to add two integers.
        System.out.println("Adding: " + numberToAdd + " to " + currentSum); // Print the addition operation being performed.
        int newSum = currentSum + numberToAdd; // Perform the addition.
        System.out.println("Returning sum of " + newSum + " to client"); // Print the resulting sum.
        return newSum; // Return the new sum to the caller.
    }
}
