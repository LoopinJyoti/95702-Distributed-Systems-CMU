package ds.client;

import ds.message.RequestMessage;
import ds.message.ResponseMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: 30th October 2024
 *
 * This class implements a proxy client for interacting with a blockchain server.
 * The client is responsible for generating RSA signatures for requests and sending them
 * to the server. It also handles displaying the blockchain menu, processing user input,
 * and receiving responses from the server.
 *
 * The client communicates with the server over TCP/IP and sends requests in JSON format.
 * It supports operations such as viewing blockchain status, adding transactions, verifying
 * the blockchain, corrupting blocks, and repairing the chain.
 *
 * Note : This code is generated from assistance with Perplexity AI
 */

/**
 * SigningClientProxy class
 *
 * This class acts as a proxy client that signs requests using RSA and sends them to a blockchain server.
 * It allows users to interact with the blockchain by selecting various operations from a menu.
 */
public class SigningClientTCP implements IBlockchainClient {
    private RealBlockchainClient realClient;
    private RSASignatureUtils rsaUtils;
    private String clientId;

    /**
     * Constructor for SigningClientProxy.
     *
     * Initializes the RSA utilities and computes the client ID based on public keys.
     *
     * @throws Exception If an error occurs during RSA key generation or client ID computation.
     */
    public SigningClientTCP() throws Exception {
        this.rsaUtils = new RSASignatureUtils();
        this.clientId = rsaUtils.computeClientId();
        this.realClient = new RealBlockchainClient(clientId);
    }
    /**
     * Displays the client's public keys (e, n) and client ID.
     */
    @Override
    public void displayKeys() {
        System.out.println("Public Key (e, n): (" + rsaUtils.getPublicKeyE() + ", " + rsaUtils.getPublicKeyN() + ")");
        System.out.println("Client ID: " + clientId);
    }

    @Override
    public void displayMenu() {
        System.out.println("\nBlockchain Menu:");
        System.out.println("0. View basic blockchain status.");
        System.out.println("1. Add a transaction to the blockchain.");
        System.out.println("2. Verify the blockchain.");
        System.out.println("3. View the blockchain.");
        System.out.println("4. Corrupt the chain.");
        System.out.println("5. Hide corruption by repairing the chain.");
        System.out.println("6. Exit.");
        System.out.print("Enter your choice: ");
    }
    /**
     * Creates a signed request based on user input from the menu.
     *
     * @param choice  The user's choice from the menu.
     * @param scanner A Scanner object to read user input.
     * @return A signed RequestMessage object containing operation details and signature.
     * @throws Exception If an error occurs during request creation or signing.
     */
    @Override
    public RequestMessage createRequest(int choice, Scanner scanner) throws Exception {
        String data = "";
        int difficulty = 0;

        switch (choice) {
            case 1:
                // Add a transaction to the blockchain
                System.out.print("Enter difficulty > 1: ");
                difficulty = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter transaction: ");
                data = scanner.nextLine();
                break;
            case 4:
                // Corrupt the chain
                System.out.print("Enter block ID to corrupt: ");
                int blockId = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                System.out.print("Enter new data for block " + blockId + ": ");
                String newData = scanner.nextLine();
                data = blockId + "," + newData;
                break;
            default:
                break;
        }

        // Generate signature using RSA
        String signature = rsaUtils.sign(data + difficulty);

        return new RequestMessage(choice, data, difficulty, clientId, rsaUtils.getPublicKeyE(), rsaUtils.getPublicKeyN(), signature);
    }
    /**
     * Processes the response received from the server and displays it to the user.
     *
     * @param response The ResponseMessage object received from the server.
     */
    @Override
    public void processResponse(ResponseMessage response) {
        // Display response data from server
        System.out.println(response.getData());

        if (response.getExecutionTime() > 0) {
            System.out.println("Execution time: " + response.getExecutionTime() + " milliseconds");
        }
    }
    /**
     * Runs the client by displaying keys, showing menu options, handling user input,
     * sending requests to the server, and processing responses.
     *
     * @throws Exception If an error occurs during communication with the server or request processing.
     */
    @Override
    public void run() throws Exception {
        displayKeys();

        try (Socket socket = new Socket("localhost", 7777);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            while (true) {
                displayMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (choice == 6) { // Exit option
                    break;
                }

                RequestMessage request = createRequest(choice, scanner);
                out.println(request.toJson());

                String responseJson = in.readLine();

                if (responseJson == null) {
                    break; // Handle server disconnection gracefully
                }

                ResponseMessage response = ResponseMessage.fromJson(responseJson);
                processResponse(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

        public static void main(String[] args) throws Exception {
            IBlockchainClient client = new SigningClientTCP();
            client.run();
        }
    }
