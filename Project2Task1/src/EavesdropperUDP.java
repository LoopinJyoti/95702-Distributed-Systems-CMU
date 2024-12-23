/**
 * DISTRIBUTED SYSTEMS, FALL 2024 - PROJECT 2
 * Author: JYOTI KHANCHANDANI
 * ANDREW ID : JKHANCHA
 * Last Modified: 2nd October 2024
 *
 * This program implements a UDP eavesdropper that intercepts communication between a client and a server.
 * It listens on a specified port for incoming packets from the client, forwards them to the server,
 * and then intercepts the server's response before sending it back to the client.
 *
 * Key features:
 * 1. Intercepts and displays all messages between client and server.
 * 2. Modifies messages containing the word "like" to "dislike".
 * 3. Runs indefinitely, continuing to intercept messages even after client-server communication ends.
 * 4. Uses two DatagramSockets: one for listening to the client and another for forwarding to the server.
 *
 * The program flow:
 * 1. Prompts the user for the listening port and the server port.
 * 2. Sets up two DatagramSockets: listenSocket (for client communication) and forwardSocket (for server communication).
 * 3. Enters an infinite loop to continuously intercept and forward messages.
 * 4. For each iteration:
 *    a. Receives a packet from the client.
 *    b. Displays the intercepted message.
 *    c. Modifies the message if it contains "like".
 *    d. Forwards the (possibly modified) message to the server.
 *    e. Receives the server's response.
 *    f. Displays the intercepted server response.
 *    g. Forwards the server's response back to the client.
 * 5. Handles IOExceptions and ensures proper closure of sockets.
 *
 * This program demonstrates:
 * - UDP socket programming in Java.
 * - Intercepting and modifying network traffic.
 * - Asynchronous communication in a client-server model.
 * - Basic string manipulation and pattern matching.
 */

import java.net.*;
import java.io.*;

public class EavesdropperUDP {
    public static void main(String[] args) {
        DatagramSocket listenSocket = null;
        DatagramSocket forwardSocket = null;
        System.out.println("EavesdropperUDP is running...");

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter the port to listen on: ");
            int listenPort = Integer.parseInt(reader.readLine());
            System.out.print("Enter the server port to forward to: ");
            int serverPort = Integer.parseInt(reader.readLine());

            listenSocket = new DatagramSocket(listenPort);
            forwardSocket = new DatagramSocket();

            while (true) {
                // Receive packet from client
                byte[] buffer = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                listenSocket.receive(packet);

                // Display the intercepted message from client
                String message = new String(packet.getData(), 0, packet.getLength()).trim();
                System.out.println("Intercepted message from client: " + message);

                // Modify message if it contains "like"
                if (message.matches(".*\\blike\\b.*")) {
                    message = message.replaceFirst("\\blike\\b", "dislike");
                    System.out.println("Modified message: " + message);
                }

                // Forward packet to the actual server
                byte[] interceptedMessageBytes = message.getBytes();
                InetAddress serverAddress = InetAddress.getByName("localhost");
                DatagramPacket forwardPacket = new DatagramPacket(interceptedMessageBytes, interceptedMessageBytes.length, serverAddress, serverPort);
                forwardSocket.send(forwardPacket);

                // Receive response from server
                byte[] replyBuffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(replyBuffer, replyBuffer.length);
                forwardSocket.receive(reply);

                // Display the intercepted reply from server
                String replyString = new String(reply.getData(), 0, reply.getLength()).trim();
                System.out.println("Intercepted reply from server: " + replyString);

                // Forward the reply to the client without modification
                DatagramPacket replyToClient = new DatagramPacket(reply.getData(), reply.getLength(), packet.getAddress(), packet.getPort());
                listenSocket.send(replyToClient);
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (listenSocket != null) listenSocket.close();
            if (forwardSocket != null) forwardSocket.close();
        }
    }
}
