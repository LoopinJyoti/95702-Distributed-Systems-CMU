 /**

 Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 Last Modified: October 31, 2024
 This program implements a TCP client for interacting with a blockchain simulator.
 It provides a user interface for performing various blockchain operations through
 the BlockchainServiceProxy.
 Key features:
 Displays a menu of available blockchain operations
 Handles user input and delegates operations to the BlockchainServiceProxy
 Presents server responses to the user
 Uses a try-with-resources block for proper resource management
 The client demonstrates the use of the Proxy pattern by interacting with the
 blockchain through the BlockchainServiceProxy, which handles all network communication.

  NOTE : This code is generated with assisstance from Perplexity AI.
 */

 package ds.client;

import ds.message.RequestMessage;
import ds.message.ResponseMessage;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientTCP {
    public static void main(String[] args) throws IOException {
        try (BlockchainServiceProxy proxy = new BlockchainServiceProxy();
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                String result = "";
                switch (choice) {
                    case 0:
                        result = proxy.getBlockchainStatus();
                        break;
                    case 1:
                        System.out.print("Enter difficulty > 1: ");
                        int difficulty = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter transaction: ");
                        String data = scanner.nextLine();
                        result = proxy.addTransaction(data, difficulty);
                        break;
                    case 2:
                        result = proxy.verifyBlockchain();
                        break;
                    case 3:
                        result = proxy.viewBlockchain();
                        break;
                    case 4:
                        System.out.print("Enter block ID to corrupt: ");
                        int blockId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter new data for the block: ");
                        String newData = scanner.nextLine();
                        result = proxy.corruptChain(blockId, newData);
                        break;
                    case 5:
                        result = proxy.repairChain();
                        break;
                    case 6:
                        return;
                    default:
                        result = "Invalid choice";
                }

                System.out.println(result);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
// displays menu for selection by client.
    private static void displayMenu() {
        System.out.println("\nBlock Chain Menu:");
        System.out.println("0. View basic blockchain status.");
        System.out.println("1. Add a transaction to the blockchain.");
        System.out.println("2. Verify the blockchain.");
        System.out.println("3. View the blockchain.");
        System.out.println("4. Corrupt the chain.");
        System.out.println("5. Hide the corruption by repairing the chain.");
        System.out.println("6. Exit.");
        System.out.print("Enter your choice: ");

    }
}
