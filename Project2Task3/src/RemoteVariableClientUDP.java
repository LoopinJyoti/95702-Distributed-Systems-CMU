/**
 * Author: JYOTI KHANCHANDANI
 * Last Modified: 7th October
 *
 * This program demonstrates a UDP client that interacts with a remote server to manipulate a sum stored on the server-side.
 * The client allows users to add to, subtract from, or get the sum based on their unique user ID.
 * Each request is encoded into a UDP packet using a specific format (user ID, value, operation type) and sent to the server.
 * The server performs the operation and responds back with the updated sum or current sum value.
 * The client uses the DatagramSocket class to handle UDP-style communication.
 * This program showcases marshaling of request data using ByteBuffer and simple UDP packet handling.
 *  Citation: Initial development of this code was influenced by generative AI guidance from Perplexity.
 */

import java.io.*; // Import for handling I/O operations.
import java.net.*; // Import for networking classes.
import java.nio.ByteBuffer; // Import for byte manipulation using buffers.

public class RemoteVariableClientUDP { // Class definition for the client program.
    private static InetAddress serverAddress; // Variable to store the server's IP address.
    private static int serverPort; // Variable to store the server's port number.
    private static DatagramSocket aSocket = null; // Socket to handle UDP communication.

    // Operation codes used to identify the type of operation (add, subtract, get).
    private static final int ADD = 1; // Operation code for adding a value.
    private static final int SUBTRACT = 2; // Operation code for subtracting a value.
    private static final int GET = 3; // Operation code for retrieving the current sum.

    public static void main(String args[]) { // Main entry point of the client program.
        try { // Start of try block to handle I/O exceptions.
            System.out.println("The client is running."); // Notify that the client is up and running.

            serverAddress = InetAddress.getByName("localhost"); // Resolve the server's IP address as "localhost".

            System.out.print("Please enter server port: "); // Prompt the user to enter the server's port number.
            BufferedReader portInput = new BufferedReader(new InputStreamReader(System.in)); // Create a BufferedReader to read port input.
            serverPort = Integer.parseInt(portInput.readLine()); // Convert the user input into an integer for the port number.

            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in)); // BufferedReader for reading further user inputs.

            while (true) { // Infinite loop to continually accept user commands.
                displayMenu(); // Display the menu options to the user.
                int choice = Integer.parseInt(typed.readLine()); // Read the user's menu choice.

                if (choice == 4) { // If the user chooses to exit.
                    System.out.println("Client side quitting. The remote variable server is still running."); // Notify user of exit.
                    break; // Exit the while loop to terminate the client.
                }

                System.out.print("Enter your ID: "); // Prompt user to enter their unique ID.
                int userId = Integer.parseInt(typed.readLine()); // Read and store the user ID.

                String result; // Variable to store the result of server operations.

                switch (choice) { // Switch statement to handle different menu choices.
                    case 1: // Case for adding a value.
                        System.out.print("Enter value to add: "); // Prompt for the value to add.
                        int addValue = Integer.parseInt(typed.readLine()); // Read and store the value to be added.
                        result = add(userId, addValue); // Call the add method with the user ID and value.
                        break;
                    case 2: // Case for subtracting a value.
                        System.out.print("Enter value to subtract: "); // Prompt for the value to subtract.
                        int subtractValue = Integer.parseInt(typed.readLine()); // Read and store the value to be subtracted.
                        result = subtract(userId, subtractValue); // Call the subtract method with the user ID and value.
                        break;
                    case 3: // Case for retrieving the sum.
                        result = get(userId); // Call the get method with the user ID.
                        break;
                    default: // If the user enters an invalid choice.
                        System.out.println("Invalid choice. Please try again."); // Notify the user of invalid input.
                        continue; // Continue the loop to display the menu again.
                }

                System.out.println("The result is " + result + "."); // Print the result of the server operation.
                System.out.println(); // Print an empty line for better readability.
            }
        } catch (IOException e) { // Catch block for I/O exceptions.
            System.out.println("IO Exception: " + e.getMessage()); // Print the exception message.
        }
    }

    private static void displayMenu() { // Method to display the menu options.
        System.out.println("1. Add a value to your sum."); // Option to add a value.
        System.out.println("2. Subtract a value from your sum."); // Option to subtract a value.
        System.out.println("3. Get your sum."); // Option to retrieve the current sum.
        System.out.println("4. Exit client."); // Option to exit the client.
    }

    private static String add(int userId, int value) throws IOException { // Method to add a value to the sum.
        return sendRequest(userId, value, ADD); // Send a request with the ADD operation.
    }

    private static String subtract(int userId, int value) throws IOException { // Method to subtract a value from the sum.
        return sendRequest(userId, value, SUBTRACT); // Send a request with the SUBTRACT operation.
    }

    private static String get(int userId) throws IOException { // Method to get the current sum.
        return sendRequest(userId, 0, GET);  // 0 as a placeholder value for GET operation.
    }

    private static String sendRequest(int userId, int value, int operation) throws IOException { // Method to send a request to the server.
        if (aSocket == null) { // If the socket is not created yet.
            aSocket = new DatagramSocket(); // Create a new DatagramSocket for communication.
        }
        ByteBuffer buffer = ByteBuffer.allocate(12);  // Allocate 12 bytes for the request (3 integers * 4 bytes each).
        buffer.putInt(userId); // Put the user ID into the buffer.
        buffer.putInt(value); // Put the value into the buffer.
        buffer.putInt(operation); // Put the operation code into the buffer.

        byte[] message = buffer.array(); // Convert the buffer to a byte array.

        DatagramPacket request = new DatagramPacket(message, message.length, serverAddress, serverPort); // Create a DatagramPacket for the request.
        aSocket.send(request); // Send the request packet to the server.

        byte[] receiveBuffer = new byte[1000]; // Create a buffer to store the server's response.
        DatagramPacket reply = new DatagramPacket(receiveBuffer, receiveBuffer.length); // Prepare a DatagramPacket to receive the reply.
        aSocket.receive(reply); // Receive the reply from the server.

        return new String(reply.getData(), 0, reply.getLength()).trim(); // Convert the received data to a string and return.
    }
}
