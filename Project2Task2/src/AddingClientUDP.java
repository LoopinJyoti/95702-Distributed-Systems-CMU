/**
 * Author: JYOTI KHANCHANDANI
 * Last Modified: 3rd October 2024
 * <p>
 * This program implements a UDP client for an adding service.
 * The client allows users to input integers which are sent to a UDP server
 * for addition. The server maintains a running sum and returns the updated
 * sum after each addition.
 * <p>
 * Key features of this UDP client:
 * 1. User interaction: Prompts for server port and integers to add.
 * 2. Consistent session management: Uses a single DatagramSocket to ensure
 * the same source port is used throughout the session.
 * 3. Separation of concerns: Communication logic is encapsulated in the 'add' method.
 * 4. Error handling: Manages IO exceptions and invalid user inputs.
 * 5. Graceful termination: Allows the user to exit by entering 'halt!'.
 * <p>
 * The program demonstrates:
 * - Basic UDP socket programming in Java
 * - User input handling and parsing
 * - Encapsulation of network communication logic
 * - Maintaining a consistent client-server session over UDP
 * <p>
 * Usage:
 * 1. Run the program
 * 2. Enter the server's port number when prompted
 * 3. Input integers to be added by the server
 * 4. Enter 'halt!' to exit the program
 * <p>
 * Note: This client is designed to work with a corresponding UDP server
 * that performs addition operations.
 *
 * @apiNote This implementation was assisted by ChatGPT (OpenAI, 2024) and Perplexity AI
 * on October 3, 2024.
 */

import java.io.BufferedReader; // Import for reading input from the console.
import java.io.IOException; // Import for handling I/O exceptions.
import java.io.InputStreamReader; // Import for converting byte streams to character streams.
import java.net.DatagramPacket; // Import for creating UDP packets.
import java.net.DatagramSocket; // Import for creating UDP sockets.
import java.net.InetAddress; // Import for handling IP addresses.

public class AddingClientUDP { // Define a class named AddingClientUDP for the UDP client.

    private static InetAddress serverAddress; // Declare a static variable for storing the server address.
    private static int serverPort; // Declare a static variable for storing the server port.
    private static DatagramSocket socket = null; // Declare a DatagramSocket for sending and receiving data.
    private static int halt = 0; // Variable to track whether the client should halt communication.

    public static void main(String[] args) { // Main entry point of the program.
        try { // Start of try block to handle I/O exceptions.
            System.out.println("The UDP client is running"); // Print message indicating client is running.

            serverAddress = InetAddress.getByName("localhost"); // Resolve the server address as "localhost".

            System.out.print("Enter server port number: "); // Prompt user to enter the server's port number.
            BufferedReader portInput = new BufferedReader(new InputStreamReader(System.in)); // Read the server port input.
            serverPort = Integer.parseInt(portInput.readLine()); // Convert the input into an integer for the port.

            System.out.println("Enter integers to add. Enter 'halt!' to exit."); // Instructions for the user on input format.
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in)); // Read user input for numbers to send.
            String nextLine; // Variable to store the user's input.

            while ((nextLine = typed.readLine()) != null) { // Continuously read user input until null (EOF).
                if ("halt!".equals(nextLine)) { // Check if the user wants to terminate the client.
                    halt = 1; // Set halt flag to 1, indicating termination.
                    System.out.println("UDP Client side quitting"); // Print message indicating client shutdown.
                    add(0); // Call add with 0 to notify the server of client shutdown.
                    break; // Exit the loop to end the program.
                }

                try { // Start a try block to handle number parsing.
                    int number = Integer.parseInt(nextLine); // Parse the input string into an integer.
                    int result = add(number); // Call the add method to send the number to the server and get the sum.
                    System.out.println("The server returned: " + result); // Print the sum received from the server.
                } catch (NumberFormatException e) { // Catch block for invalid number formats.
                    System.out.println("Invalid input. Please enter an integer."); // Print error message for non-integer input.
                }

                System.out.println("Enter the next integer to add or 'halt!' to exit"); // Prompt user for the next input.
            }
        } catch (IOException e) { // Catch block for I/O exceptions.
            System.out.println("IO Exception: " + e.getMessage()); // Print the I/O exception message.
        }
    }

    public static int add(int number) throws IOException { // Method to send a number to the server and receive the sum.
        if (halt == 0) { // Check if the client is not in halt state.
            if (socket == null) { // If the socket is not yet created.
                socket = new DatagramSocket(); // Create a new DatagramSocket.
            }

            byte[] m = String.valueOf(number).getBytes(); // Convert the number into a byte array.
            DatagramPacket request = new DatagramPacket(m, m.length, serverAddress, serverPort); // Create a DatagramPacket with the number.
            socket.send(request); // Send the packet to the server.

            byte[] buffer = new byte[1000]; // Create a buffer for the response from the server.
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length); // Prepare a packet to receive the server's reply.
            socket.receive(reply); // Receive the reply from the server.

            return Integer.parseInt(new String(reply.getData(), 0, reply.getLength()).trim()); // Convert the reply data to integer and return.
        } else { // If the client is in halt state.
            socket.close(); // Close the socket.
            return 0; // Return 0 indicating termination.
        }

    }
}

